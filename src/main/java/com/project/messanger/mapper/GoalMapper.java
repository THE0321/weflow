package com.project.messanger.mapper;

import com.project.messanger.dto.GoalDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoalMapper {
    long insertGoal(GoalDto goalDto);

    int updateGoal(GoalDto goalDto);

    int deleteGoal(long goalIdx);
}
