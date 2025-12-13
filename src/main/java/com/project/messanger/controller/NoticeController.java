package com.project.messanger.controller;

import com.project.messanger.dto.NoticeDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.NoticeService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class NoticeController {
    private final NoticeService noticeService;
    private final AuthUtil authUtil;

    public NoticeController(NoticeService noticeService, AuthUtil authUtil) {
        this.noticeService = noticeService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getNoticeList(HttpServletRequest request,
                                             @RequestParam(value = "page", required = false) int page,
                                             @RequestParam(value = "limit", required = false) int limit,
                                             @RequestParam(value = "title", required = false) String title) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("title", title);
            param.put("is_admin", authUtil.authCheck(session, true));

            result.put("success", true);
            result.put("list", noticeService.getNoticeList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "공지사항을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/detail")
    public Map<String, Object> getNoticeDetail(HttpServletRequest request,
                                               @RequestParam("notice_idx") long noticeIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("notice_idx", noticeIdx);
            param.put("is_admin", authUtil.authCheck(session, true));

            result.put("success", true);
            result.put("detail", noticeService.getNoticeDetail(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "공지사항 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertNotice(HttpServletRequest request,
                                               @RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "content", required = false) String content,
                                               @RequestParam(value = "link_url", required = false) String linkUrl,
                                               @RequestParam(value = "start_date", required = false) String startDate,
                                               @RequestParam(value = "end_date", required = false) String endDate,
                                               @RequestParam(value = "is_active", required = false) String isActive){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo.getAdminYn().equals("N")) {
            result.put("success", false);
            result.put("error", "관리자만 등록 가능합니다.");

            return result;
        }

        try {
            // NoticeDto 객체 생성
            NoticeDto noticeDto = NoticeDto.builder()
                    .title(title)
                    .content(content)
                    .linkUrl(linkUrl)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isActive(isActive)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            long noticeIdx = noticeService.insertNotice(noticeDto);

            result.put("success", true);
            result.put("idx", noticeIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "공지사항을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateNotice(HttpServletRequest request,
                                            @RequestParam(value = "notice_idx", required = false) long noticeIdx,
                                            @RequestParam(value = "title", required = false) String title,
                                            @RequestParam(value = "content", required = false) String content,
                                            @RequestParam(value = "link_url", required = false) String linkUrl,
                                            @RequestParam(value = "start_date", required = false) String startDate,
                                            @RequestParam(value = "end_date", required = false) String endDate,
                                            @RequestParam(value = "is_active", required = false) String isActive){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session, true)) {
            result.put("success", false);
            result.put("error", "공지사항을 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // NoticeDto 객체 생성
            NoticeDto noticeDto = NoticeDto.builder()
                    .noticeIdx(noticeIdx)
                    .title(title)
                    .content(content)
                    .linkUrl(linkUrl)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isActive(isActive)
                    .build();

            // 공지사항 수정
            int success = noticeService.updateNotice(noticeDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "공지사항을 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "공지사항을 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteNotice(HttpServletRequest request,
                                            @RequestParam("notice_idx") long noticeIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session, true)) {
            result.put("success", false);
            result.put("error", "공지사항을 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 체크리스트 삭제
            int success = noticeService.deleteNotice(noticeIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "체크리스트를 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트를 삭제하는데 실패했습니다.");
        }

        return result;
    }
}
