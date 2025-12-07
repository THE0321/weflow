package com.project.messanger.controller;

import com.project.messanger.dto.MeetingRoomDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.MeetingRoomService;
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
@RequestMapping("/meeting")
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;
    private final AuthUtil authUtil;

    public MeetingRoomController(MeetingRoomService meetingRoomService, AuthUtil authUtil) {
        this.meetingRoomService = meetingRoomService;
        this.authUtil = authUtil;
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

            long roomIdx = meetingRoomService.insertMeetingRoom(meetingRoomDto);

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
            // MeetingRoomDto 객체 생성
            MeetingRoomDto meetingRoomDto = MeetingRoomDto.builder()
                    .roomIdx(roomIdx)
                    .name(name)
                    .location(location)
                    .capacity(capacity)
                    .build();

            // 회의실 수정
            int success = meetingRoomService.updateMeetingRoom(meetingRoomDto);

            result.put("success", success == 1);
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
    public Map<String, Object> deleteNotice(HttpServletRequest request,
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
            // 회의실 삭제
            int success = meetingRoomService.deleteMeetingRoom(roomIdx);

            result.put("success", success == 1);
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
