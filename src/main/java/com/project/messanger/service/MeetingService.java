package com.project.messanger.service;

import com.project.messanger.dto.MeetingAttenderLinkDto;
import com.project.messanger.dto.MeetingRoomDto;
import com.project.messanger.dto.ReservationDto;
import com.project.messanger.mapper.MeetingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     * return List<ReservationDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return meetingMapper.getReservationList(param);
    }

    /*
     * get date list
     * @param Map<String, Object>
     * return List<ReservationDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getDateList(Map<String, Object> param) {
        return meetingMapper.getDateList(param);
    }

    /*
     * get reservation by idx
     * @param long
     * return ReservationDto
     */
    @Transactional(readOnly = true)
    public ReservationDto getReservationByIdx(Map<String, Object> param) {
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
     * return List<ScheduleDto>
     */
    @Transactional(readOnly = true)
    public List<MeetingAttenderLinkDto> getMeetingAttenderLink(long reservationIdx) {
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
                .map(MeetingAttenderLinkDto::getUserIdx)
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
    public int deleteMeetingAttenderLink(long reservationIdx, List<Long> deleteLinkIdxList) {
        List<MeetingAttenderLinkDto> meetingAttenderList = getMeetingAttenderLink(reservationIdx);

        // 이미 참석 확정한 목록 제거
        List<Long> attenderIdxList = new ArrayList<>();
        for (MeetingAttenderLinkDto meetingAttenderLinkDto : meetingAttenderList) {
            if (meetingAttenderLinkDto.getIsAttend().equals("Y")) {
                attenderIdxList.add(meetingAttenderLinkDto.getUserIdx());
            }
        }

        for (int i = 0; i < deleteLinkIdxList.size(); i++) {
            if (attenderIdxList.contains(deleteLinkIdxList.get(i))) {
                deleteLinkIdxList.remove(i);
            }
        }

        if (deleteLinkIdxList.isEmpty()) {
            return 0;
        }

        return meetingMapper.deleteMeetingAttenderLink(deleteLinkIdxList);
    }

    /*
     * get meeting room by idx
     * @param long
     * return MeetingRoomDto
     */
    @Transactional
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
