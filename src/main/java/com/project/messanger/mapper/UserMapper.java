package com.project.messanger.mapper;

import com.project.messanger.dto.TeamDto;
import com.project.messanger.dto.TeamUserLinkDto;
import com.project.messanger.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    List<UserDto> getUserList(Map<String, Object> param);

    List<Long> getUserListByTeamIdx(long teamIdx);

    UserDto getUserByIdx(long userIdx);

    UserDto getUserByEmail(String email);

    long insertUser(UserDto userDto);

    int updateUser(UserDto userDto);

    int deleteUser(long userIdx);

    List<TeamDto> getTeamList(Map<String, Object> param);

    List<Long> getTeamListByUserIdx(long userIdx);

    TeamDto getTeamByIdx(long teamIdx);

    long insertTeam(String teamName);

    long updateTeam(TeamDto teamDto);

    int deleteTeam(long teamIdx);

    int insertTeamUserLink(List<TeamUserLinkDto> list);

    int deleteTeamUserLink(List<Long> list);
}
