package com.project.messanger.mapper;

import com.project.messanger.dto.GoalDto;
import com.project.messanger.dto.GoalLogDto;
import com.project.messanger.dto.GoalUserLinkDto;
import com.project.messanger.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoalMapper {
    List<GoalDto> getGoalList(Map<String, Object> param);

    GoalDto getGoalByIdx(long goalIdx);

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
