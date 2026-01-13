package com.project.messanger.service;

import com.project.messanger.dto.*;
import com.project.messanger.mapper.GoalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalService {
    private final GoalMapper goalMapper;

    public GoalService(GoalMapper goalMapper) {
        this.goalMapper = goalMapper;
    }

    /*
     * get goal count
     * @param Map<String, Object>
     * return long
     */
    public long getGoalCount(Map<String, Object> param) {
        return goalMapper.getGoalCount(param);
    }

    /*
     * get goal list
     * @param Map<String, Object>
     * return List<GoalAndLogDto>
     */
    public List<GoalAndLogDto> getGoalList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return goalMapper.getGoalList(param);
    }

    /*
     * get goal main list
     * @param Map<String, Object>
     * return List<GoalAndLogDto>
     */
    public List<GoalAndLogDto> getGoalMainList(Map<String, Object> param) {
        return goalMapper.getGoalMainList(param);
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
     * get goal graph
     * @param Map<String, Object>
     * return goalDto
     */
    @Transactional(readOnly = true)
    public HashMap<String, Object> getGoalGraph(Map<String, Object> param) {
        return goalMapper.getGoalGraph(param);
    }

    /*
     * get goal log
     * @param long
     * return List<GoalLogWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<GoalLogWithUserDto> getGoalLog(long goalIdx) {
        return goalMapper.getGoalLog(goalIdx);
    }

    /*
     * get goal user link
     * @param long
     * return List<GoalUserLinkWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<GoalUserLinkWithUserDto> getGoalUserLink(long goalIdx) {
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
     * delete goal user link by user idx
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteGoalUserLinkByUserIdx(long goalIdx, List<Long> deleteUserIdxList) {
        Map<String, Object> param = new HashMap<>();
        param.put("goal_idx", goalIdx);
        param.put("user_idx_list", deleteUserIdxList);

        return goalMapper.deleteGoalUserLinkByUserIdx(param);
    }

    /*
     * delete goal user link by team idx
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteGoalUserLinkByTeamIdx(long goalIdx, List<Long> deleteTeamIdxList) {
        Map<String, Object> param = new HashMap<>();
        param.put("goal_idx", goalIdx);
        param.put("team_idx_list", deleteTeamIdxList);

        return goalMapper.deleteGoalUserLinkByTeamIdx(param);
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

    /*
     * insert goal user link by team idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertGoalUserLinkByTeamIdx(long goalIdx, List<Long> teamIdxList) {
        List<GoalUserLinkDto> valueList = new ArrayList<>();
        List<Long> goalUserList = new ArrayList<>(getGoalUserLink(goalIdx).stream()
                .map(GoalUserLinkWithUserDto::getTeamIdx)
                .toList());

        // 값 리스트
        for (long teamIdx : teamIdxList) {
            if (goalUserList.contains(teamIdx)) {
                continue;
            }

            GoalUserLinkDto goalUserLinkDto = GoalUserLinkDto.builder()
                    .goalIdx(goalIdx)
                    .teamIdx(teamIdx)
                    .build();

            valueList.add(goalUserLinkDto);
            goalUserList.add(teamIdx);
        }

        return insertGoalUserLink(valueList);
    }

    /*
     * insert goal user link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertGoalUserLinkByUserIdx(long goalIdx, List<Long> userIdxList) {
        List<GoalUserLinkDto> valueList = new ArrayList<>();
        List<Long> goalUserList = new ArrayList<>(getGoalUserLink(goalIdx).stream()
                .map(GoalUserLinkWithUserDto::getUserIdx)
                .toList());

        // 값 리스트
        for (long userIdx : userIdxList) {
            if (goalUserList.contains(userIdx)) {
                continue;
            }

            GoalUserLinkDto goalUserLinkDto = GoalUserLinkDto.builder()
                    .goalIdx(goalIdx)
                    .userIdx(userIdx)
                    .build();

            valueList.add(goalUserLinkDto);
            goalUserList.add(userIdx);
        }

        if (valueList.isEmpty()) {
            return 0;
        }

        return insertGoalUserLink(valueList);
    }
}
