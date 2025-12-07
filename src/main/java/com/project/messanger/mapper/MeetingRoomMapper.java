package com.project.messanger.mapper;

import com.project.messanger.dto.MeetingRoomDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetingRoomMapper {
    long insertMeetingRoom(MeetingRoomDto meetingRoomDto);

    int updateMeetingRoom(MeetingRoomDto meetingRoomDto);

    int deleteMeetingRoom(long roomIdx);
}
