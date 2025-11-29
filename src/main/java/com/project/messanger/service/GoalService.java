package com.project.messanger.service;

import com.project.messanger.dto.*;
import com.project.messanger.mapper.GoalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoalService {
    private final GoalMapper goalMapper;

    public GoalService(GoalMapper goalMapper) {
        this.goalMapper = goalMapper;
    }

    /*
     * get goal list
     * @param Map<String, Object>
     * return List<UserDto>
     */
    public List<GoalAndLogDto> getGoalList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return goalMapper.getGoalList(param);
    }

    /*
     * get goal by goal idx
     * @param long
     * return goalDto
     */
    @Transactional(readOnly = true)
    public GoalAndLogDto getGoalByIdx(long goalIdx) {
        return goalMapper.getGoalByIdx(goalIdx);
    }

    /*
     * get goal log
     * @param long
     * return List<goalLogDto>
     */
    @Transactional(readOnly = true)
    public List<GoalLogDto> getGoalLog(long goalIdx) {
        return goalMapper.getGoalLog(goalIdx);
    }

    /*
     * get goal user link
     * @param long
     * return List<GoalUserLinkDto>
     */
    @Transactional(readOnly = true)
    public List<GoalUserLinkDto> getGoalUserLink(long goalIdx) {
        return goalMapper.getGoalUserLink(goalIdx);
    }

    /*
     * insert goal
     * @param GoalDto
     * return long
     */
    @Transactional
    public long insertGoal(GoalDto goalDto) {
        return goalMapper.insertGoal(goalDto);
    }

    /*
     * update goal
     * @param GoalDto
     * return int
     */
    @Transactional
    public int updateGoal(GoalDto goalDto) {
        return goalMapper.updateGoal(goalDto);
    }

    /*
     * delete goal
     * @param long
     * return int
     */
    @Transactional
    public int deleteGoal(long goalIdx) {
        return goalMapper.deleteGoal(goalIdx);
    }

    /*
     * insert goal user link
     * @param List<GoalUserLinkDto>
     * return int
     */
    @Transactional
    public int insertGoalUserLink(List<GoalUserLinkDto> list) {
        return goalMapper.insertGoalUserLink(list);
    }

    /*
     * delete goal user link
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteGoalUserLink(List<Long> deleteLinkIdxList) {
        return goalMapper.deleteGoalUserLink(deleteLinkIdxList);
    }

    /*
     * insert goal user link by team idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertGoalUserLinkByTeamIdx(long goalIdx, List<Long> teamIdxList) {
        List<GoalUserLinkDto> insertList = new ArrayList<>();

        // 값 리스트
        for (long teamIdx : teamIdxList) {
            GoalUserLinkDto goalUserLinkDto = GoalUserLinkDto.builder()
                    .goalIdx(goalIdx)
                    .teamIdx(teamIdx)
                    .build();
            insertList.add(goalUserLinkDto);
        }

        return insertGoalUserLink(insertList);
    }

    /*
     * insert goal user link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertGoalUserLinkByUserIdx(long goalIdx, List<Long> userIdxList) {
        List<GoalUserLinkDto> insertList = new ArrayList<>();

        // 값 리스트
        for (long userIdx : userIdxList) {
            GoalUserLinkDto goalUserLinkDto = GoalUserLinkDto.builder()
                    .goalIdx(goalIdx)
                    .userIdx(userIdx)
                    .build();
            insertList.add(goalUserLinkDto);
        }

        return insertGoalUserLink(insertList);
    }

    /*
     * insert goal log
     * @param GoalLogDto
     * return long
     */
    @Transactional
    public long insertGoalLog(GoalLogDto goalLogDto) {
        return goalMapper.insertGoalLog(goalLogDto);
    }

    /*
     * update goal log
     * @param GoalLogDto
     * return int
     */
    @Transactional
    public int updateGoalLog(GoalLogDto goalLogDto) {
        return goalMapper.updateGoalLog(goalLogDto);
    }

    /*
     * delete goal log
     * @param long
     * return int
     */
    @Transactional
    public int deleteGoalLog(long logIdx) {
        return goalMapper.deleteGoalLog(logIdx);
    }
}
