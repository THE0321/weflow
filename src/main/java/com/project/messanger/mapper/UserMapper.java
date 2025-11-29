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

    UserDto getUserByIdx(long userIdx);

    UserDto getUserByEmail(String email);

    List<Long> getTeamListByUserIdx(long userIdx);

    long insertUser(UserDto userDto);

    int updateUser(UserDto userDto);

    int deleteUser(long userIdx);

    long insertTeam(String teamName);

    long updateTeam(TeamDto teamDto);

    int deleteTeam(long teamIdx);

    int insertTeamUserLink(List<TeamUserLinkDto> list);

    int deleteTeamUserLink(List<Long> list);
}
