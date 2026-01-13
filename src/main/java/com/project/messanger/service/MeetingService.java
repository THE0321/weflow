package com.project.messanger.service;

import com.project.messanger.dto.*;
import com.project.messanger.mapper.MeetingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeetingService {
    private final MeetingMapper meetingMapper;
    public MeetingService(MeetingMapper meetingMapper) {
        this.meetingMapper = meetingMapper;
    }

    /*
     * get reservation list
     * @param Map<String, Object>
     * return List<ReservationWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationWithUserDto> getReservationList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return meetingMapper.getReservationList(param);
    }

    /*
     * get reservation list by month
     * @param Map<String, Object>
     * return List<ReservationDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationListByMonth(Map<String, Object> param) {
        return meetingMapper.getReservationListByMonth(param);
    }

    /*
     * get reservation by idx
     * @param long
     * return ReservationWithUserDto
     */
    @Transactional(readOnly = true)
    public ReservationWithUserDto getReservationByIdx(Map<String, Object> param) {
        return meetingMapper.getReservationByIdx(param);
    }

    /*
     * insert reservation
     * @param ReservationDto
     * return long
     */
    @Transactional
    public long insertReservation(ReservationDto reservationDto) {
        return meetingMapper.insertReservation(reservationDto);
    }

    /*
     * update reservation
     * @param ReservationDto
     * return int
     */
    @Transactional
    public int updateReservation(ReservationDto reservationDto) {
        return meetingMapper.updateReservation(reservationDto);
    }

    /*
     * delete reservation
     * @param long
     * return int
     */
    @Transactional
    public int deleteReservation(long reservationIdx) {
        return meetingMapper.deleteReservation(reservationIdx);
    }

    /*
     * get schedule attender link
     * @param Map<String, Object>
     * return List<MeetingAttenderLinkWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<MeetingAttenderLinkWithUserDto> getMeetingAttenderLink(long reservationIdx) {
        return meetingMapper.getMeetingAttenderLink(reservationIdx);
    }

    /*
     * get meeting attender link by idx
     * @param long
     * return MeetingAttenderLinkDto
     */
    @Transactional
    public MeetingAttenderLinkDto getMeetingAttenderLinkByIdx(long linkIdx) {
        return meetingMapper.getMeetingAttenderLinkByIdx(linkIdx);
    }

    /*
     * insert meeting attender link
     * @param List<MeetingAttenderLinkDto>
     * return int
     */
    @Transactional
    public int insertMeetingAttenderLink(List<MeetingAttenderLinkDto> list) {
        return meetingMapper.insertMeetingAttenderLink(list);
    }

    /*
     * update meeting attender link
     * @param MeetingAttenderLinkDto
     * return int
     */
    @Transactional
    public int updateMeetingAttenderLink(MeetingAttenderLinkDto meetingAttenderLinkDto) {
        return meetingMapper.updateMeetingAttenderLink(meetingAttenderLinkDto);
    }

    /*
     * insert meeting attender link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertMeetingAttenderLinkByUserIdx(long reservationIdx, List<Long> userIdxList, boolean isCreate) {
        List<MeetingAttenderLinkDto> valueList = new ArrayList<>();
        List<Long> meetingAttenderList = new ArrayList<>(getMeetingAttenderLink(reservationIdx).stream()
                .map(MeetingAttenderLinkWithUserDto::getUserIdx)
                .toList());

        // 값 리스트
        for (int i = 0; i < userIdxList.size(); i++) {
            if (meetingAttenderList.contains(userIdxList.get(i))) {
                continue;
            }

            MeetingAttenderLinkDto meetingAttenderLinkDto = MeetingAttenderLinkDto.builder()
                    .reservationIdx(reservationIdx)
                    .userIdx(userIdxList.get(i))
                    .isAttend("N")
                    .build();

            // 등록자는 무조건 참석
            if (isCreate && i == 0) {
                meetingAttenderLinkDto.setIsAttend("Y");
            }

            valueList.add(meetingAttenderLinkDto);
            meetingAttenderList.add(userIdxList.get(i));
        }

        if (valueList.isEmpty()) {
            return 0;
        }

        return insertMeetingAttenderLink(valueList);
    }

    /*
     * delete meeting attender link
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteMeetingAttenderLink(long reservationIdx, List<Long> deleteUserIdxList) {
        List<MeetingAttenderLinkWithUserDto> meetingAttenderList = getMeetingAttenderLink(reservationIdx);

        // 이미 참석 확정한 목록 제거
        List<Long> attenderIdxList = new ArrayList<>();
        for (MeetingAttenderLinkWithUserDto meetingAttenderLinkDto : meetingAttenderList) {
            if (meetingAttenderLinkDto.getIsAttend().equals("Y")) {
                attenderIdxList.add(meetingAttenderLinkDto.getUserIdx());
            }
        }

        for (int i = 0; i < deleteUserIdxList.size(); i++) {
            if (attenderIdxList.contains(deleteUserIdxList.get(i))) {
                deleteUserIdxList.remove(i);
            }
        }

        if (deleteUserIdxList.isEmpty()) {
            return 0;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("reservation_idx", reservationIdx);
        param.put("user_idx_list", deleteUserIdxList);

        return meetingMapper.deleteMeetingAttenderLink(param);
    }

    @Transactional(readOnly = true)
    public List<MeetingRoomDto> getMeetingRoomList() {
        return meetingMapper.getMeetingRoomList();
    }

    /*
     * get meeting room by idx
     * @param long
     * return MeetingRoomDto
     */
    @Transactional(readOnly = true)
    public MeetingRoomDto getMeetingRoomByIdx(long roomIdx) {
        return meetingMapper.getMeetingRoomByIdx(roomIdx);
    }

    /*
     * insert meeting room
     * @param MeetingRoomDto
     * return long
     */
    @Transactional
    public long insertMeetingRoom(MeetingRoomDto meetingRoomDto) {
        return meetingMapper.insertMeetingRoom(meetingRoomDto);
    }

    /*
     * update meeting room
     * @param MeetingRoomDto
     * return int
     */
    @Transactional
    public int updateMeetingRoom(MeetingRoomDto meetingRoomDto) {
        return meetingMapper.updateMeetingRoom(meetingRoomDto);
    }

    /*
     * delete meeting room
     * @param long
     * return int
     */
    @Transactional
    public int deleteMeetingRoom(long roomIdx) {
        return meetingMapper.deleteMeetingRoom(roomIdx);
    }
}
