package com.project.messanger.service;

import com.project.messanger.dto.GoalDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.mapper.GoalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
