package com.project.messanger.service;

import com.project.messanger.dto.MeetingRoomDto;
import com.project.messanger.mapper.MeetingRoomMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingRoomService {
    private MeetingRoomMapper meetingRoomMapper;

    public MeetingRoomService(MeetingRoomMapper meetingRoomMapper) {
        this.meetingRoomMapper = meetingRoomMapper;
    }

    /*
     * insert meeting room
     * @param MeetingRoomDto
     * return long
     */
    @Transactional
    public long insertMeetingRoom(MeetingRoomDto meetingRoomDto) {
        return meetingRoomMapper.insertMeetingRoom(meetingRoomDto);
    }

    /*
     * update meeting room
     * @param MeetingRoomDto
     * return int
     */
    @Transactional
    public int updateMeetingRoom(MeetingRoomDto meetingRoomDto) {
        return meetingRoomMapper.updateMeetingRoom(meetingRoomDto);
    }

    /*
     * delete meeting room
     * @param long
     * return int
     */
    @Transactional
    public int deleteMeetingRoom(long roomIdx) {
        return meetingRoomMapper.deleteMeetingRoom(roomIdx);
    }
}
