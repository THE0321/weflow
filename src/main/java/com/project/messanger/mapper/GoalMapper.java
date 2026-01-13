package com.project.messanger.mapper;

import com.project.messanger.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoalMapper {
    long getGoalCount(Map<String, Object> param);

    List<GoalAndLogDto> getGoalList(Map<String, Object> param);

    List<GoalAndLogDto> getGoalMainList(Map<String, Object> param);

    GoalAndLogDto getGoalByIdx(long goalIdx);

    HashMap<String, Object> getGoalGraph(Map<String, Object> param);

    List<GoalLogWithUserDto> getGoalLog(long logIdx);

    List<GoalUserLinkWithUserDto> getGoalUserLink(long goalIdx);

    long insertGoal(GoalDto goalDto);

    int updateGoal(GoalDto goalDto);

    int deleteGoal(long goalIdx);

    int insertGoalUserLink(List<GoalUserLinkDto> list);

    int deleteGoalUserLinkByUserIdx(Map<String, Object> param);

    int deleteGoalUserLinkByTeamIdx(Map<String, Object> param);

    long insertGoalLog(GoalLogDto goalLogDto);

    int updateGoalLog(GoalLogDto goalLogDto);

    int deleteGoalLog(long logIdx);
}
