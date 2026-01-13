package com.project.messanger.mapper;

import com.project.messanger.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingMapper {
    List<ReservationWithUserDto> getReservationList(Map<String, Object> param);

    List<ReservationDto> getReservationListByMonth(Map<String, Object> param);

    ReservationWithUserDto getReservationByIdx(Map<String, Object> param);

    long insertReservation(ReservationDto reservationDto);

    int updateReservation(ReservationDto reservationDto);

    int deleteReservation(long reservationIdx);

    List<MeetingAttenderLinkWithUserDto> getMeetingAttenderLink(long reservationIdx);

    MeetingAttenderLinkDto getMeetingAttenderLinkByIdx(long linkIdx);

    int updateMeetingAttenderLink(MeetingAttenderLinkDto meetingAttenderLinkDto);

    int insertMeetingAttenderLink(List<MeetingAttenderLinkDto> list);

    int deleteMeetingAttenderLink(Map<String, Object> param);

    List<MeetingRoomDto> getMeetingRoomList();

    MeetingRoomDto getMeetingRoomByIdx(long roomIdx);

    long insertMeetingRoom(MeetingRoomDto meetingRoomDto);

    int updateMeetingRoom(MeetingRoomDto meetingRoomDto);

    int deleteMeetingRoom(long roomIdx);
}
