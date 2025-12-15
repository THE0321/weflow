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
                                                      @RequestParam(value = "page", required = false) int page,
                                                      @RequestParam(value = "limit", required = false) int limit) {
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
            param.put("user_idx", authUtil.getLoginInfo(session).getUserIdx());

            result.put("success", true);
            result.put("list", chattingService.getChattingMessageList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 불러올 수 없습니다.");
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
            param.put("user_idx", authUtil.getLoginInfo(session).getUserIdx());

            result.put("success", true);
            result.put("list", chattingService.getChattingList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertChattingMessage(HttpServletRequest request,
                                                     MultipartHttpServletRequest multipartHttpServletRequest,
                                                     @RequestParam(value = "chatting_idx") long chattingIdx,
                                                     @RequestParam(value = "content") String content,
                                                     @RequestParam(value = "user_idx") List<Long> userIdxList){
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

            if (multipartHttpServletRequest.getFile("file") != null) {
                FileDto fileDto = fileService.uploadFile(multipartHttpServletRequest);
                fileDto.setUserIdx(loginInfo.getUserIdx());

                chattingMessageDto.setFileIdx(fileService.insertFile(fileDto));
            }

            long chattingMessageIdx = chattingService.insertChattingMessage(chattingMessageDto);

            result.put("success", true);
            result.put("idx", chattingMessageIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "채팅방을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/room/create")
    public Map<String, Object> insertChatting(HttpServletRequest request,
                                              @RequestParam(value = "name") String name,
                                              @RequestParam(value = "user_idx") List<Long> userIdxList){
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
            if (userIdxList != null) {
                userIdxList.add(0, chattingDto.getCreatorIdx());
                chattingService.insertChattingUserLinkByUserIdx(chattingIdx, userIdxList);
            }

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
                                              @RequestParam(value = "name", required = false) String name){
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

    @PostMapping("/user/create")
    public Map<String, Object> insertChattingUserLinkByUserIdx(HttpServletRequest request,
                                                               @RequestParam(value = "chatting_idx") long chattingIdx,
                                                               @RequestParam(value = "user_idx") long userIdx){
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
            param.put("user_idx", authUtil.getLoginInfo(session).getUserIdx());
            ChattingDto chattingDto = chattingService.getChattingByIdx(param);
            if (chattingDto == null) {
                result.put("error", "멤버를 추가하는데 실패했습니다.");

                return result;
            }

            // 멤버 추가
            int success = chattingService.insertChattingUserLinkByUserIdx(chattingIdx, userIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "멤버를 추가하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "멤버를 추가하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/user/delete")
    public Map<String, Object> deleteChattingUserLink(HttpServletRequest request,
                                                      @RequestParam(value = "link_idx") long linkIdx) {
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
            param.put("user_idx", authUtil.getLoginInfo(session).getUserIdx());
            ChattingDto chattingDto = chattingService.getChattingByIdx(param);
            if (chattingDto == null) {
                result.put("error", "멤버를 삭제하는데 실패했습니다.");

                return result;
            }

            // 멤버 삭제
            int success = chattingService.deleteChattingUserLink(linkIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "멤버를 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "멤버를 삭제하는데 실패했습니다.");
        }

        return result;
    }
}
