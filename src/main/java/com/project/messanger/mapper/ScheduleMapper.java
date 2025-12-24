package com.project.messanger.mapper;

import com.project.messanger.dto.ScheduleAttenderLinkDto;
import com.project.messanger.dto.ScheduleDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScheduleMapper {
    List<ScheduleDto> getScheduleList(Map<String, Object> param);

    List<ScheduleDto> getDateList(Map<String, Object> param);

    ScheduleDto getScheduleByIdx(Map<String, Object> param);

    long insertSchedule(ScheduleDto scheduleDto);

    int updateSchedule(ScheduleDto scheduleDto);

    int deleteSchedule(long scheduleIdx);

    List<ScheduleAttenderLinkDto> getScheduleAttenderLinkList(long scheduleIdx);

    ScheduleAttenderLinkDto getScheduleAttenderLinkByIdx(long linkIdx);

    int insertScheduleAttenderLink(List<ScheduleAttenderLinkDto> list);

    int deleteScheduleAttenderLink(List<Long> list);

    int updateScheduleAttender(ScheduleAttenderLinkDto scheduleAttenderLinkDto);
}
