package com.project.messanger.controller;

import com.project.messanger.dto.*;
import com.project.messanger.service.ChattingService;
import com.project.messanger.service.FileService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatting")
public class ChattingController {
    private final ChattingService chattingService;
    private final FileService fileService;
    private final AuthUtil authUtil;

    public ChattingController(ChattingService chattingService, FileService fileService, AuthUtil authUtil) {
        this.chattingService = chattingService;
        this.fileService = fileService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getChattingMessageList(HttpServletRequest request,
                                                      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                                      @RequestParam(value = "chatting_idx") Long chattingIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("chatting_idx", chattingIdx);
            param.put("user_idx", loginInfo.getUserIdx());

            result.put("success", true);
            result.put("list", chattingService.getChattingMessageList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertChattingMessage(HttpServletRequest request,
                                                     @RequestParam(value = "file", required = false) MultipartHttpServletRequest multipartHttpServletRequest,
                                                     @RequestParam(value = "chatting_idx") long chattingIdx,
                                                     @RequestParam(value = "content") String content){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // ChattingDto 객체 생성
            ChattingMessageDto chattingMessageDto = ChattingMessageDto.builder()
                    .chattingIdx(chattingIdx)
                    .content(content)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            if (multipartHttpServletRequest != null && multipartHttpServletRequest.getFile("file") != null) {
                FileDto fileDto = fileService.uploadFile(multipartHttpServletRequest);
                fileDto.setCreatorIdx(loginInfo.getUserIdx());

                chattingMessageDto.setFileIdx(fileService.insertFile(fileDto));
            }

            long chattingMessageIdx = chattingService.insertChattingMessage(chattingMessageDto);

            result.put("success", true);
            result.put("idx", chattingMessageIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/room/list")
    public Map<String, Object> getChattingList(HttpServletRequest request,
                                               @RequestParam(value = "name", required = false) String name) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("name", name);
            param.put("user_idx", loginInfo.getUserIdx());

            // 채팅방 목록 조회
            List<ChattingDto> chattingList = chattingService.getChattingList(param);

            result.put("success", true);
            result.put("list", chattingList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/room/detail")
    public Map<String, Object> getChattingDetail(HttpServletRequest request,
                                                 @RequestParam(value = "chatting_idx") long chattingIdx){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 채팅 조회
            ChattingDto chattingDto = chattingService.getChattingByIdx(chattingIdx);
            if (chattingDto == null) {
                result.put("success", false);
                result.put("error", "채팅방 상세를 불러올 수 없습니다.");

                return result;
            }

            List<ChattingUserLinkWithUserDto> chattingUserLinkList = chattingService.getChattingUserLink(chattingIdx);
            if (!loginInfo.getAdminYn().equals("Y") && chattingDto.getCreatorIdx() != loginInfo.getUserIdx()) {
                List<Long> chattingUserIdxList = new ArrayList<>(chattingUserLinkList.stream()
                        .map(ChattingUserLinkWithUserDto::getUserIdx)
                        .toList());

                if (!chattingUserIdxList.contains(loginInfo.getUserIdx())) {
                    result.put("success", false);
                    result.put("error", "채팅방을 불러올 수 없습니다.");

                    return result;
                }
            }

            result.put("success", true);
            result.put("detail", chattingDto);
            result.put("user_list", chattingUserLinkList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/room/create")
    public Map<String, Object> insertChatting(HttpServletRequest request,
                                                  @RequestParam(value = "name") String name,
                                                  @RequestParam(value = "user_idx", required = false) List<Long> userIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // ChattingDto 객체 생성
            ChattingDto chattingDto = ChattingDto.builder()
                    .name(name)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            long chattingIdx = chattingService.insertChatting(chattingDto);

            // 채팅 멤버 추가
            if (userIdxList == null) {
                userIdxList = new ArrayList<>();
            }

            userIdxList.addFirst(chattingDto.getCreatorIdx());
            chattingService.insertChattingUserLinkByUserIdx(chattingIdx, userIdxList);

            result.put("success", true);
            result.put("idx", chattingIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/room/modify")
    public Map<String, Object> updateChatting(HttpServletRequest request,
                                              @RequestParam(value = "chatting_idx") long chattingIdx,
                                              @RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                              @RequestParam(value = "delete_user_idx", required = false) List<Long> deleteUserIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // ChattingDto 객체 생성
            ChattingDto chattingDto = ChattingDto.builder()
                    .name(name)
                    .chattingIdx(chattingIdx)
                    .build();

            // 채팅방 수정
            int success = chattingService.updateChatting(chattingDto);

            // 채팅 멤버 추가
            if (userIdxList != null) {
                chattingService.insertChattingUserLinkByUserIdx(chattingIdx, userIdxList);
            }

            // 채팅 멤버 삭제
            if (deleteUserIdxList != null) {
                chattingService.deleteChattingUserLink(chattingIdx, deleteUserIdxList);
            }

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "채팅방을 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/room/delete")
    public Map<String, Object> deleteChatting(HttpServletRequest request,
                                              @RequestParam(value = "chatting_idx") long chattingIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 채팅방 삭제
            int success = chattingService.deleteChatting(chattingIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "채팅방을 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 삭제하는데 실패했습니다.");
        }

        return result;
    }
}
