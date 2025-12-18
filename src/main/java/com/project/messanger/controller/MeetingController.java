package com.project.messanger.controller;

import com.project.messanger.dto.*;
import com.project.messanger.service.MeetingService;
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
@RequestMapping("/meeting")
public class MeetingController {
    private final MeetingService meetingService;
    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    public MeetingController(MeetingService meetingService, NotificationService notificationService, AuthUtil authUtil) {
        this.meetingService = meetingService;
        this.notificationService = notificationService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getReservationList(HttpServletRequest request,
                                                  @RequestParam(value = "page", required = false) int page,
                                                  @RequestParam(value = "limit", required = false) int limit,
                                                  @RequestParam(value = "description", required = false) String description,
                                                  @RequestParam(value = "reservation_date", required = false) String reservationDate,
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
            param.put("description", description);
            param.put("reservation_date", reservationDate);
            if (my) {
                param.put("user_idx", loginInfo.getUserIdx());
            }

            // 회의실 조회
            List<ReservationDto> reservationList = meetingService.getReservationList(param);
            boolean isEmpty = reservationList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", reservationList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실을 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/date/list")
    public Map<String, Object> getDateList(HttpServletRequest request,
                                           @RequestParam(value = "reservation_date", required = false) String reservationDate) {
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
            param.put("reservation_date", reservationDate);

            // 권한 체크
            if (loginInfo.getAdminYn().equals("N")) {
                param.put("user_idx", loginInfo.getUserIdx());
            }

            // 회원 목록 조회
            List<ReservationDto> dateList = meetingService.getDateList(param);
            boolean isEmpty = dateList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", dateList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "날짜 리스트를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/detail")
    public Map<String, Object> getReservationByIdx(HttpServletRequest request,
                                                   @RequestParam("reservation_idx") long reservationIdx) {
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
            param.put("reservation_idx", reservationIdx);

            // 권한 체크
            if (loginInfo.getAdminYn().equals("N")) {
                param.put("user_idx", loginInfo.getUserIdx());
            }

            // 회의실 조회
            ReservationDto reservationDto = meetingService.getReservationByIdx(param);

            result.put("success", reservationDto != null);
            if (reservationDto == null) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                // 회의 참석자 조회
                List<MeetingAttenderLinkDto> scheduleAttenderList = meetingService.getMeetingAttenderLink(reservationIdx);

                result.put("detail", reservationDto);
                result.put("attender_list", scheduleAttenderList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertReservation(HttpServletRequest request,
                                                 @RequestParam(value = "room_idx") long roomIdx,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "start_date") String startDate,
                                                 @RequestParam(value = "end_date") String endDate,
                                                 @RequestParam(value = "creator_idx", required = false) long creatorIdx,
                                                 @RequestParam(value = "approver_idx", required = false) long approverIdx,
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
            // Dto 객체 생성
            ReservationDto reservationDto = ReservationDto.builder()
                    .roomIdx(roomIdx)
                    .description(description)
                    .startDate(startDate)
                    .endDate(endDate)
                    .approverIdx(approverIdx)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            if (loginInfo.getAdminYn().equals("Y")) {
                if (creatorIdx != 0) {
                    reservationDto.setCreatorIdx(creatorIdx);
                }
                reservationDto.setApproverIdx(loginInfo.getUserIdx());
            }

            // 회의 등록
            long reservationIdx = meetingService.insertReservation(reservationDto);

            // 회의 참석자 등록
            userIdxList.add(0, reservationDto.getCreatorIdx());
            meetingService.insertMeetingAttenderLinkByUserIdx(reservationIdx, userIdxList);

            // 알림 등록
            NotificationDto notificationDto = NotificationDto.builder()
                    .type("RESERVATION")
                    .content("일정이 등록되었습니다.")
                    .linkUrl("/meeting/" + reservationIdx)
                    .build();

            notificationService.insertNotificationByUserIdx(notificationDto, userIdxList);

            result.put("success", true);
            result.put("idx", reservationIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateReservation(HttpServletRequest request,
                                                 @RequestParam(value = "reservation_idx", required = false) long reservationIdx,
                                                 @RequestParam(value = "room_idx", required = false) long roomIdx,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "start_date") String startDate,
                                                 @RequestParam(value = "end_date") String endDate,
                                                 @RequestParam(value = "creator_idx", required = false) long creatorIdx,
                                                 @RequestParam(value = "approver_idx", required = false) long approverIdx,
                                                 @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                                 @RequestParam(value = "delete_idx", required = false) List<Long> deleteIdxList){
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
            param.put("reservation_idx", reservationIdx);

            // 수정할 데이터 확인
            ReservationDto reservationInfo = meetingService.getReservationByIdx(param);
            if (reservationInfo == null) {
                result.put("success", false);
                result.put("error", "수정할 데이터가 없습니다.");

                return result;
            }

            // 관리자가 아닌 경우 등록자인지 확인
            if (loginInfo.getAdminYn().equals("N")) {
                if (reservationInfo.getCreatorIdx() != loginInfo.getUserIdx()) {
                    result.put("success", false);
                    result.put("error", "회의실 예약을 수정하는데 실패했습니다.");

                    return result;
                }
            }

            // ReservationDto 객체 생성
            ReservationDto reservationDto = ReservationDto.builder()
                    .roomIdx(roomIdx)
                    .description(description)
                    .startDate(startDate)
                    .endDate(endDate)
                    .approverIdx(approverIdx)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            // 회의실 예약 수정
            int success = meetingService.updateReservation(reservationDto);

            // 회의 참석자 추가
            if (userIdxList != null) {
                userIdxList.add(0, reservationDto.getCreatorIdx());
                meetingService.insertMeetingAttenderLinkByUserIdx(reservationIdx, userIdxList);
            }

            // 회의 참석자 삭제
            if (deleteIdxList != null) {
                meetingService.deleteMeetingAttenderLink(reservationIdx, deleteIdxList);
            }

            result.put("success", success != 0);
            if (success == 0) {
                result.put("error", "회의실 예약을 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실 예약을 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteReservation(HttpServletRequest request,
                                                 @RequestParam("reservation_idx") long reservationIdx) {
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
            param.put("reservation_idx", reservationIdx);

            // 삭제할 데이터 확인
            ReservationDto reservationInfo = meetingService.getReservationByIdx(param);
            if (reservationInfo == null) {
                result.put("success", false);
                result.put("error", "삭제할 데이터가 없습니다.");

                return result;
            }

            // 관리자가 아닌 경우 등록자인지 확인
            if (loginInfo.getAdminYn().equals("N")) {
                if (reservationInfo.getCreatorIdx() != loginInfo.getUserIdx()) {
                    result.put("success", false);
                    result.put("error", "회의실 예약을 삭제하는데 실패했습니다.");

                    return result;
                }
            }

            // 회의 삭제
            int success = meetingService.deleteReservation(reservationIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "회의실 예약을 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실 예약을 삭제하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/attend")
    public Map<String, Object> updateMeetingAttender(HttpServletRequest request,
                                                     @RequestParam(value = "link_idx") long linkIdx,
                                                     @RequestParam(value = "is_attender") String isAttender){
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
            MeetingAttenderLinkDto beforeData = meetingService.getMeetingAttenderLinkByIdx(linkIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "승인할 데이터가 없습니다.");

                return result;
            }

            // MeetingAttenderLinkDto 객체 생성
            MeetingAttenderLinkDto meetingAttenderLinkDto = MeetingAttenderLinkDto.builder()
                    .linkIdx(linkIdx)
                    .userIdx(loginInfo.getUserIdx())
                    .isAttender(isAttender)
                    .build();

            // 회의 참석여부 수정
            int success = meetingService.updateMeetingAttenderLink(meetingAttenderLinkDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "회의 참석여부를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의 참석여부를 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/room/create")
    public Map<String, Object> insertMeetingRoom(HttpServletRequest request,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "location", required = false) String location,
                                                 @RequestParam(value = "capacity", required = false) int capacity){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        if (!authUtil.authCheck(session, true)) {
            result.put("success", false);
            result.put("error", "관리자만 등록 가능합니다.");

            return result;
        }

        try {
            // MeetingRoomDto 객체 생성
            MeetingRoomDto meetingRoomDto = MeetingRoomDto.builder()
                    .name(name)
                    .location(location)
                    .capacity(capacity)
                    .build();

            long roomIdx = meetingService.insertMeetingRoom(meetingRoomDto);

            result.put("success", true);
            result.put("idx", roomIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/room/modify")
    public Map<String, Object> updateMeetingRoom(HttpServletRequest request,
                                                 @RequestParam(value = "room_idx") long roomIdx,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "location", required = false) String location,
                                                 @RequestParam(value = "capacity", required = false) int capacity){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session, true)) {
            result.put("success", false);
            result.put("error", "회의실을 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // 수정할 데이터 확인
            MeetingRoomDto beforeData = meetingService.getMeetingRoomByIdx(roomIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "수정할 데이터가 없습니다.");

                return result;
            }

            // MeetingRoomDto 객체 생성
            MeetingRoomDto meetingRoomDto = MeetingRoomDto.builder()
                    .roomIdx(roomIdx)
                    .name(name)
                    .location(location)
                    .capacity(capacity)
                    .build();

            // 회의실 수정
            int success = meetingService.updateMeetingRoom(meetingRoomDto);

            result.put("success", success != 0);
            if (success == 0) {
                result.put("error", "회의실을 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실을 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/room/delete")
    public Map<String, Object> deleteMeetingRoom(HttpServletRequest request,
                                            @RequestParam("room_idx") long roomIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session, true)) {
            result.put("success", false);
            result.put("error", "회의실을 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 삭제할 데이터 확인
            MeetingRoomDto beforeData = meetingService.getMeetingRoomByIdx(roomIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "삭제할 데이터가 없습니다.");

                return result;
            }

            // 회의실 삭제
            int success = meetingService.deleteMeetingRoom(roomIdx);

            result.put("success", success != 0);
            if (success == 0) {
                result.put("error", "회의실을 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회의실을 삭제하는데 실패했습니다.");
        }

        return result;
    }
}
