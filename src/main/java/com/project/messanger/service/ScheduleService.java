package com.project.messanger.service;

import com.project.messanger.dto.GoalUserLinkDto;
import com.project.messanger.dto.NoticeDto;
import com.project.messanger.dto.ScheduleAttenderLinkDto;
import com.project.messanger.dto.ScheduleDto;
import com.project.messanger.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {
    private ScheduleMapper scheduleMapper;

    public ScheduleService(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    /*
     * get schedule list
     * @param Map<String, Object>
     * return List<ScheduleDto>
     */
    @Transactional(readOnly = true)
    public List<ScheduleDto> getScheduleList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return scheduleMapper.getScheduleList(param);
    }

    /*
     * get date list
     * @param Map<String, Object>
     * return List<ScheduleDto>
     */
    @Transactional(readOnly = true)
    public List<ScheduleDto> getDateList(Map<String, Object> param) {
        return scheduleMapper.getDateList(param);
    }

    /*
     * get schedule by idx
     * @param long
     * return ScheduleDto
     */
    @Transactional(readOnly = true)
    public ScheduleDto getScheduleByIdx(Map<String, Object> param) {
        return scheduleMapper.getScheduleByIdx(param);
    }

    /*
     * insert schedule
     * @param ScheduleDto
     * return long
     */
    @Transactional
    public long insertSchedule(ScheduleDto scheduleDto) {
        return scheduleMapper.insertSchedule(scheduleDto);
    }

    /*
     * update schedule
     * @param ScheduleDto
     * return int
     */
    @Transactional
    public int updateSchedule(ScheduleDto scheduleDto) {
        return scheduleMapper.updateSchedule(scheduleDto);
    }

    /*
     * delete schedule
     * @param long
     * return int
     */
    @Transactional
    public int deleteSchedule(long scheduleIdx) {
        return scheduleMapper.deleteSchedule(scheduleIdx);
    }

    /*
     * insert schedule attender link
     * @param List<ScheduleAttenderLinkDto>
     * return int
     */
    @Transactional
    public int insertScheduleAttenderLink(List<ScheduleAttenderLinkDto> list) {
        return scheduleMapper.insertScheduleAttenderLink(list);
    }

    /*
     * insert schedule attender link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertScheduleAttenderLinkByUserIdx(long scheduleIdx, List<Long> userIdxList) {
        List<ScheduleAttenderLinkDto> insertList = new ArrayList<>();

        // 값 리스트
        for (int i = 0; i < userIdxList.size(); i++) {
            ScheduleAttenderLinkDto scheduleAttenderLinkDto = ScheduleAttenderLinkDto.builder()
                    .scheduleIdx(scheduleIdx)
                    .userIdx(userIdxList.get(i))
                    .isAttend("N")
                    .build();

            // 등록자는 무조건 참석
            if (i == 0) {
                scheduleAttenderLinkDto.setIsAttend("Y");
            }

            insertList.add(scheduleAttenderLinkDto);
        }

        return insertScheduleAttenderLink(insertList);
    }

    /*
     * delete schedule attender link
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteScheduleAttenderLink(List<Long> deleteLinkIdxList) {
        return scheduleMapper.deleteScheduleAttenderLink(deleteLinkIdxList);
    }
}
