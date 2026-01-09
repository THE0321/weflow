package com.project.messanger.mapper;

import com.project.messanger.dto.TeamDto;
import com.project.messanger.dto.TeamUserLinkDto;
import com.project.messanger.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    long getUserCount(Map<String, Object> param);

    List<UserDto> getUserList(Map<String, Object> param);

    List<UserDto> getAllUserList();

    List<UserDto> getUserListByTeamIdx(long teamIdx);

    UserDto getUserByIdx(long userIdx);

    UserDto getUserByEmail(String email);

    long insertUser(UserDto userDto);

    int updateUser(UserDto userDto);

    int deleteUser(long userIdx);

    long getTeamCount(Map<String, Object> param);

    List<TeamDto> getTeamList(Map<String, Object> param);

    List<TeamDto> getAllTeamList();

    List<TeamDto> getTeamListByUserIdx(long userIdx);

    TeamDto getTeamByIdx(long teamIdx);

    long insertTeam(TeamDto teamDto);

    long updateTeam(TeamDto teamDto);

    int deleteTeam(long teamIdx);

    int insertTeamUserLink(List<TeamUserLinkDto> list);

    int deleteTeamUserLinkByUserIdx(Map<String, Object> param);

    int deleteTeamUserLinkByTeamIdx(Map<String, Object> param);

}
