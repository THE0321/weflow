package com.project.messanger.mapper;

import com.project.messanger.dto.ScheduleAttenderLinkDto;
import com.project.messanger.dto.ScheduleAttenderLinkWithUserDto;
import com.project.messanger.dto.ScheduleDto;
import com.project.messanger.dto.ScheduleWithUserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface ScheduleMapper {
    long getScheduleCount(Map<String, Object> param);

    List<ScheduleWithUserDto> getScheduleList(Map<String, Object> param);

    List<ScheduleDto> getScheduleListByMonth(Map<String, Object> param);

    ScheduleWithUserDto getScheduleByIdx(Map<String, Object> param);

    int insertSchedule(ScheduleDto scheduleDto);

    int updateSchedule(ScheduleDto scheduleDto);

    int deleteSchedule(long scheduleIdx);

    List<ScheduleAttenderLinkWithUserDto> getScheduleAttenderLinkList(long scheduleIdx);

    ScheduleAttenderLinkDto getScheduleAttenderLinkByIdx(long linkIdx);

    int insertScheduleAttenderLink(List<ScheduleAttenderLinkDto> list);

    int deleteScheduleAttenderLink(Map<String, Object> param);

    int updateScheduleAttender(ScheduleAttenderLinkDto scheduleAttenderLinkDto);
}
