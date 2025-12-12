package com.project.messanger.mapper;

import com.project.messanger.dto.MeetingAttenderLinkDto;
import com.project.messanger.dto.MeetingRoomDto;
import com.project.messanger.dto.ReservationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingMapper {
    List<ReservationDto> getReservationList(Map<String, Object> param);

    List<ReservationDto> getDateList(Map<String, Object> param);

    ReservationDto getReservationByIdx(Map<String, Object> param);

    long insertReservation(ReservationDto reservationDto);

    int updateReservation(ReservationDto reservationDto);

    int deleteReservation(long reservationIdx);

    List<MeetingAttenderLinkDto> getMeetingAttenderLink(long reservationIdx);

    int updateMeetingAttenderLink(MeetingAttenderLinkDto meetingAttenderLinkDto);

    int insertMeetingAttenderLink(List<MeetingAttenderLinkDto> list);

    int deleteMeetingAttenderLink(List<Long> list);

    long insertMeetingRoom(MeetingRoomDto meetingRoomDto);

    int updateMeetingRoom(MeetingRoomDto meetingRoomDto);

    int deleteMeetingRoom(long roomIdx);
}
