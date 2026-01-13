package com.project.messanger.service;

import com.project.messanger.dto.*;
import com.project.messanger.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {
    private final ScheduleMapper scheduleMapper;

    public ScheduleService(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    /*
     * get schedule count
     * @param Map<String, Object>
     * return long
     */
    @Transactional(readOnly = true)
    public long getScheduleCount(Map<String, Object> param) {
        return scheduleMapper.getScheduleCount(param);
    }

    /*
     * get schedule list
     * @param Map<String, Object>
     * return List<ScheduleWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<ScheduleWithUserDto> getScheduleList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return scheduleMapper.getScheduleList(param);
    }

    /*
     * get schedule list by month
     * @param Map<String, Object>
     * return List<ScheduleDto>
     */
    @Transactional(readOnly = true)
    public List<ScheduleDto> getScheduleListByMonth(Map<String, Object> param) {
        return scheduleMapper.getScheduleListByMonth(param);
    }

    /*
     * get schedule by idx
     * @param long
     * return ScheduleWithUserDto
     */
    @Transactional(readOnly = true)
    public ScheduleWithUserDto getScheduleByIdx(Map<String, Object> param) {
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
     * get schedule attender link
     * @param Map<String, Object>
     * return List<ScheduleAttenderLinkWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<ScheduleAttenderLinkWithUserDto> getScheduleAttenderLinkList(long scheduleIdx) {
        return scheduleMapper.getScheduleAttenderLinkList(scheduleIdx);
    }

    /*
     * get schedule by idx
     * @param long
     * return ScheduleDto
     */
    @Transactional(readOnly = true)
    public ScheduleAttenderLinkDto getScheduleAttenderLinkByIdx(long linkIdx) {
        return scheduleMapper.getScheduleAttenderLinkByIdx(linkIdx);
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
    public int insertScheduleAttenderLinkByUserIdx(long scheduleIdx, List<Long> userIdxList, boolean isCreate) {
        List<ScheduleAttenderLinkDto> valueList = new ArrayList<>();
        List<Long> scheduleAttenderList = new ArrayList<>(getScheduleAttenderLinkList(scheduleIdx).stream()
                .map(ScheduleAttenderLinkWithUserDto::getUserIdx)
                .toList());

        // 값 리스트
        for (int i = 0; i < userIdxList.size(); i++) {
            if (scheduleAttenderList.contains(userIdxList.get(i))) {
                continue;
            }

            ScheduleAttenderLinkDto scheduleAttenderLinkDto = ScheduleAttenderLinkDto.builder()
                    .scheduleIdx(scheduleIdx)
                    .userIdx(userIdxList.get(i))
                    .isAttend("N")
                    .build();

            // 등록자는 무조건 참석
            if (isCreate && i == 0) {
                scheduleAttenderLinkDto.setIsAttend("Y");
            }

            valueList.add(scheduleAttenderLinkDto);
            scheduleAttenderList.add(userIdxList.get(i));
        }

        return insertScheduleAttenderLink(valueList);
    }

    /*
     * update schedule attender
     * @param List<Long>
     * return int
     */
    @Transactional
    public int updateScheduleAttender(ScheduleAttenderLinkDto scheduleAttenderLinkDto) {
        return scheduleMapper.updateScheduleAttender(scheduleAttenderLinkDto);
    }

    /*
     * delete schedule attender link
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteScheduleAttenderLink(long scheduleIdx, List<Long> deleteUserIdxList) {
        List<ScheduleAttenderLinkWithUserDto> scheduleAttenderList = getScheduleAttenderLinkList(scheduleIdx);

        // 이미 참석 확정한 목록 제거
        List<Long> attenderIdxList = new ArrayList<>();
        for (ScheduleAttenderLinkWithUserDto scheduleAttenderLinkDto : scheduleAttenderList) {
            if (scheduleAttenderLinkDto.getIsAttend().equals("Y")) {
                attenderIdxList.add(scheduleAttenderLinkDto.getUserIdx());
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
        param.put("schedule_idx", scheduleIdx);
        param.put("user_idx_list", deleteUserIdxList);

        return scheduleMapper.deleteScheduleAttenderLink(param);
    }
}
