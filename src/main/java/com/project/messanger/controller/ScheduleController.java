package com.project.messanger.controller;

import com.project.messanger.dto.*;
import com.project.messanger.service.NotificationService;
import com.project.messanger.service.ScheduleService;
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
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    public ScheduleController(ScheduleService scheduleService, NotificationService notificationService, AuthUtil authUtil) {
        this.scheduleService = scheduleService;
        this.notificationService = notificationService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getScheduleList(HttpServletRequest request,
                                               @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                               @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                               @RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "schedule_date", required = false) String scheduleDate,
                                               @RequestParam(value = "my", required = false) boolean my) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        } else if (loginInfo.getAdminYn().equals("N")) {
            my = true;
        }

        try {
            // 파라미터 세팅
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("title", title);
            param.put("schedule_date", scheduleDate);
            if (my) {
                param.put("user_idx", loginInfo.getUserIdx());
            }

            // 일정 목록 조회
            List<ScheduleWithUserDto> scheduleList = scheduleService.getScheduleList(param);
            result.put("success", true);
            result.put("list", scheduleList);
            result.put("count", scheduleService.getScheduleCount(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/list/month")
    public Map<String, Object> getScheduleListByMonth(HttpServletRequest request,
                                                      @RequestParam(value = "schedule_date") String scheduleDate) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 파라미터 세팅
            Map<String, Object> param = new HashMap<>();
            param.put("schedule_date", scheduleDate);

            // 권한 체크
            if (loginInfo.getAdminYn().equals("N")) {
                param.put("user_idx", loginInfo.getUserIdx());
            }

            // 회원 목록 조회
            List<ScheduleDto> scheduleList = scheduleService.getScheduleListByMonth(param);
            result.put("success", true);
            result.put("list", scheduleList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/detail")
    public Map<String, Object> getScheduleByIdx(HttpServletRequest request,
                                                @RequestParam("schedule_idx") long scheduleIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 파라미터 세팅
            Map<String, Object> param = new HashMap<>();
            param.put("schedule_idx", scheduleIdx);

            // 권한 체크
            if (loginInfo.getAdminYn().equals("N")) {
                param.put("user_idx", loginInfo.getUserIdx());
            }

            // 일정 조회
            ScheduleWithUserDto scheduleDto = scheduleService.getScheduleByIdx(param);

            result.put("success", scheduleDto != null);
            if (scheduleDto == null) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                // 일정 참석자 조회
                List<ScheduleAttenderLinkWithUserDto> scheduleAttenderList = scheduleService.getScheduleAttenderLinkList(scheduleIdx);

                result.put("detail", scheduleDto);
                result.put("attender_list", scheduleAttenderList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertSchedule(HttpServletRequest request,
                                              @RequestParam(value = "title", required = false) String title,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam(value = "location", required = false) String location,
                                              @RequestParam(value = "start_date", required = false) String startDate,
                                              @RequestParam(value = "end_date", required = false) String endDate,
                                              @RequestParam(value = "is_allday", required = false) String isAllday,
                                              @RequestParam(value = "creator_idx", required = false) long creatorIdx,
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
            // ScheduleDto 객체 생성
            ScheduleDto scheduleDto = ScheduleDto.builder()
                    .title(title)
                    .description(description)
                    .location(location)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isAllday(isAllday)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            if (loginInfo.getAdminYn().equals("Y")) {
                if (creatorIdx != 0) {
                    scheduleDto.setCreatorIdx(creatorIdx);
                }
                scheduleDto.setApproverIdx(loginInfo.getUserIdx());
            }

            int success = scheduleService.insertSchedule(scheduleDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "일정을 등록하는데 실패했습니다.");
                return result;
            }

            long scheduleIdx = scheduleDto.getScheduleIdx();
            result.put("idx", scheduleIdx);

            userIdxList.addFirst(scheduleDto.getCreatorIdx());
            scheduleService.insertScheduleAttenderLinkByUserIdx(scheduleIdx, userIdxList, true);

            // 알림 등록
            NotificationDto notificationDto = NotificationDto.builder()
                    .type("SCHEDULE")
                    .content("일정이 등록되었습니다.")
                    .linkUrl("/schedule/" + scheduleIdx)
                    .build();

            notificationService.insertNotificationByUserIdx(notificationDto, userIdxList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateSchedule(HttpServletRequest request,
                                              @RequestParam(value = "schedule_idx") long scheduleIdx,
                                              @RequestParam(value = "title", required = false) String title,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam(value = "location", required = false) String location,
                                              @RequestParam(value = "start_date", required = false) String startDate,
                                              @RequestParam(value = "end_date", required = false) String endDate,
                                              @RequestParam(value = "is_allday", required = false) String isAllday,
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
            Map<String, Object> param = new HashMap<>();
            param.put("schedule_idx", scheduleIdx);

            // 수정할 데이터 확인
            ScheduleWithUserDto scheduleInfo = scheduleService.getScheduleByIdx(param);
            if (scheduleInfo == null) {
                result.put("success", false);
                result.put("error", "수정할 데이터가 없습니다.");

                return result;
            }

            // 관리자가 아닌 경우 등록자인지 확인
            if (loginInfo.getAdminYn().equals("N")) {
                if (scheduleInfo.getCreatorIdx() != loginInfo.getUserIdx()) {
                    result.put("success", false);
                    result.put("error", "일정을 수정하는데 실패했습니다.");

                    return result;
                }
            }

            // ScheduleDto 객체 생성
            ScheduleDto scheduleDto = ScheduleDto.builder()
                    .scheduleIdx(scheduleIdx)
                    .title(title)
                    .description(description)
                    .location(location)
                    .startDate(startDate)
                    .endDate(endDate)
                    .isAllday(isAllday)
                    .build();

            // 일정 수정
            int success = scheduleService.updateSchedule(scheduleDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "일정을 수정하는데 실패했습니다.");
            }

            // 일정 참석자 추가
            if (userIdxList != null) {
                scheduleService.insertScheduleAttenderLinkByUserIdx(scheduleIdx, userIdxList, false);
            }

            // 일정 참석자 삭제
            if (deleteUserIdxList != null) {
                scheduleService.deleteScheduleAttenderLink(scheduleIdx, deleteUserIdxList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정을 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteSchedule(HttpServletRequest request,
                                              @RequestParam("schedule_idx") long scheduleIdx) {
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
            param.put("schedule_idx", scheduleIdx);

            // 삭제할 데이터 확인
            ScheduleWithUserDto beforeData = scheduleService.getScheduleByIdx(param);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "삭제할 데이터가 없습니다.");

                return result;
            }

            // 관리자가 아닌 경우 등록자인지 확인
            if (loginInfo.getAdminYn().equals("N")) {
                if (beforeData.getCreatorIdx() != loginInfo.getUserIdx()) {
                    result.put("success", false);
                    result.put("error", "일정을 삭제하는데 실패했습니다.");

                    return result;
                }
            }

            // 체크리스트 삭제
            int success = scheduleService.deleteSchedule(scheduleIdx);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "일정을 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정을 삭제하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/approver")
    public Map<String, Object> approverSchedule(HttpServletRequest request,
                                                @RequestParam(value = "schedule_idx") long scheduleIdx){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        } else if (loginInfo.getAdminYn().equals("N")) {
            // 권한 체크
            result.put("success", false);
            result.put("error", "일정을 승인할 권한이 없습니다.");

            return result;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("schedule_idx", scheduleIdx);

            // 승인할 데이터 확인
            ScheduleWithUserDto beforeData = scheduleService.getScheduleByIdx(param);
            if(beforeData == null) {
                result.put("success", false);
                result.put("error", "승인할 데이터가 없습니다.");

                return result;
            }

            // ScheduleDto 객체 생성
            ScheduleDto scheduleDto = ScheduleDto.builder()
                    .scheduleIdx(scheduleIdx)
                    .approverIdx(loginInfo.getUserIdx())
                    .build();

            // 일정 승인
            int success = scheduleService.updateSchedule(scheduleDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "일정을 승인하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정을 승인하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/attend")
    public Map<String, Object> updateScheduleAttender(HttpServletRequest request,
                                                      @RequestParam(value = "link_idx") long linkIdx,
                                                      @RequestParam(value = "is_attend") String isAttend){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 승인할 데이터 확인
            ScheduleAttenderLinkDto beforeData = scheduleService.getScheduleAttenderLinkByIdx(linkIdx);
            if (beforeData == null || beforeData.getUserIdx() != loginInfo.getUserIdx()) {
                result.put("success", false);
                result.put("error", "승인할 데이터가 없습니다.");

                return result;
            }

            // ScheduleAttenderLinkDto 객체 생성
            ScheduleAttenderLinkDto scheduleAttenderLinkDto = ScheduleAttenderLinkDto.builder()
                    .linkIdx(linkIdx)
                    .userIdx(loginInfo.getUserIdx())
                    .isAttend(isAttend)
                    .build();

            // 일정 참석자 수정
            int success = scheduleService.updateScheduleAttender(scheduleAttenderLinkDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "일정 참석자를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "일정 참석자를 수정하는데 실패했습니다.");
        }

        return result;
    }
}
