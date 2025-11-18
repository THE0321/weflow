package com.project.messanger.service;

import com.project.messanger.dto.GoalDto;
import com.project.messanger.mapper.GoalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {
    private final GoalMapper goalMapper;

    public GoalService(GoalMapper goalMapper) {
        this.goalMapper = goalMapper;
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
     * @param List<String>
     * return int
     */
    @Transactional
    public int insertGoalUserLink(List<String> valueList) {
        return goalMapper.insertGoalUserLink(valueList);
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
     * delete goal user link by goal idx
     * @param long
     * return int
     */
    @Transactional
    public int deleteGoalUserLinkByGoalIdx(long goalIdx) {
        return goalMapper.deleteGoalUserLinkByGoalIdx(goalIdx);
    }

    /*
     * insert goal user link by team idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertGoalUserLinkByTeamIdx(long goalIdx, List<Long> teamIdxList) {
        List<String> valueList = new ArrayList<>();

        // 값 리스트
        for (long teamIdx : teamIdxList) {
            valueList.add("("+ goalIdx +", "+ teamIdx +", null)");
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
        List<String> valueList = new ArrayList<>();

        // 값 리스트
        for (long userIdx : userIdxList) {
            valueList.add("("+ goalIdx +", null, "+ userIdx +")");
        }

        return insertGoalUserLink(valueList);
    }
}
