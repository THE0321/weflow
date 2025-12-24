package com.project.messanger.controller;

import com.project.messanger.dto.NotificationDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.NotificationService;
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
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    public NotificationController(NotificationService notificationService, AuthUtil authUtil) {
        this.notificationService = notificationService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getNotificationList(HttpServletRequest request,
                                                   @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                   @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                                   @RequestParam(value = "type", required = false) String type) {
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
            param.put("type", type);
            param.put("user_idx", loginInfo.getUserIdx());

            // 알림 목록 조회
            List<NotificationDto> notificationList = notificationService.getNotificationList(param);
            boolean isEmpty = notificationList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", notificationList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "알림을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/read")
    public Map<String, Object> readNotification(HttpServletRequest request,
                                                @RequestParam("notification_idx") long notificationIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 로그인 체크
        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "알림을 불러올 수 없습니다.");

            return result;
        }

        try {
            // 수정할 데이터 확인
            NotificationDto notificationDto = notificationService.getNotificationByIdx(notificationIdx);
            if (notificationDto == null) {
                result.put("success", false);
                result.put("error", "수정할 데이터가 없습니다.");

                return result;
            }

            // 등록자인지 확인
            if (notificationDto.getCreatorIdx() != loginInfo.getUserIdx()) {
                result.put("success", false);
                result.put("error", "알림을 불러올 수 없습니다.");

                return result;
            }

            // 알림 읽음 여부 수정
            int success = notificationService.updateNotification(notificationIdx);
            result.put("success", success != 0);
            if (success == 0) {
                result.put("error", "알림을 불러올 수 없습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "알림을 불러올 수 없습니다.");
        }

        return result;
    }
}
