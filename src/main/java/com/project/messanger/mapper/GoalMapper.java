package com.project.messanger.mapper;

import com.project.messanger.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoalMapper {
    List<GoalAndLogDto> getGoalList(Map<String, Object> param);

    GoalAndLogDto getGoalByIdx(long goalIdx);

    List<GoalLogDto> getGoalLog(long logIdx);

    List<GoalUserLinkDto> getGoalUserLink(long goalIdx);

    long insertGoal(GoalDto goalDto);

    int updateGoal(GoalDto goalDto);

    int deleteGoal(long goalIdx);

    int insertGoalUserLink(List<String> list);

    int deleteGoalUserLink(List<Long> list);

    long insertGoalLog(GoalLogDto goalLogDto);

    int updateGoalLog(GoalLogDto goalLogDto);

    int deleteGoalLog(long logIdx);
}
