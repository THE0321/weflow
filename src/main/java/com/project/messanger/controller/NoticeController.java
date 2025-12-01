package com.project.messanger.controller;

import com.project.messanger.dto.ChecklistDto;
import com.project.messanger.dto.GoalDto;
import com.project.messanger.dto.NoticeDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.ChecklistService;
import com.project.messanger.service.NoticeService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
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
            result.put("error", e.getMessage());
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateNotice(HttpServletRequest request,
                                          @RequestParam(value = "notice_idx", required = false) Long noticeIdx,
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
            int success = noticeService.deleteChecklist(noticeIdx);

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
