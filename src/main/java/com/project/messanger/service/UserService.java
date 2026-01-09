package com.project.messanger.service;

import com.project.messanger.dto.TeamDto;
import com.project.messanger.dto.TeamUserLinkDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUserListByTeamIdx(long teamIdx) {
        return userMapper.getUserListByTeamIdx(teamIdx);
    }

    /*
     * get user by user idx
     * @param long
     * return userDto
     */
    @Transactional(readOnly = true)
    public UserDto getUserByIdx(long userIdx) {
        return userMapper.getUserByIdx(userIdx);
    }

    /*
     * get user by email
     * @param String
     * return userDto
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    /*
     * insert user
     * @param UserDto
     * return long
     */
    @Transactional
    public long insertUser(UserDto userDto) {
        if (userDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
        }

        return userMapper.insertUser(userDto);
    }

    /*
     * update user
     * @param UserDto
     * return int
     */
    @Transactional
    public int updateUser(UserDto userDto) {
        if (userDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
        }

        return userMapper.updateUser(userDto);
    }

    /*
     * delete user
     * @param long
     * return int
     */
    @Transactional
    public int deleteUser(long userIdx) {
        return userMapper.deleteUser(userIdx);
    }

    /*
     * get team count
     * @param Map<String, Object>
     * return long
     */
    @Transactional(readOnly = true)
    public long getTeamCount(Map<String, Object> param)
    {
        return userMapper.getTeamCount(param);
    }

    /*
     * get team list
     * @param Map<String, Object>
     * return List<TeamDto>
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return userMapper.getTeamList(param);
    }

    /*
     * get all team list
     * return List<UserDto>
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeamList() {
        return userMapper.getAllTeamList();
    }

    /*
     * get team list by user idx
     * @param long
     * return List<Long>
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamListByUserIdx(long userIdx) {
        return userMapper.getTeamListByUserIdx(userIdx);
    }

    /*
     * get team by idx
     * @param long
     * return List<Long>
     */
    @Transactional(readOnly = true)
    public TeamDto getTeamByIdx(long teamIdx) {
        return userMapper.getTeamByIdx(teamIdx);
    }

    /*
     * insert team
     * @param String
     * return long
     */
    @Transactional
    public long insertTeam(TeamDto teamDto) { return userMapper.insertTeam(teamDto); }

    /*
     * update team
     * @param TeamDto
     * return long
     */
    @Transactional
    public long updateTeam(TeamDto teamDto) { return userMapper.updateTeam(teamDto); }

    /*
     * delete team
     * @param long
     * return int
     */
    @Transactional
    public int deleteTeam(long teamIdx) { return userMapper.deleteTeam(teamIdx); }

    /*
     * get login user idx
     * @param String, String
     * return long
     */
    @Transactional(readOnly = true)
    public long getLoginUserIdx(String email, String password) {
        UserDto user = this.getUserByEmail(email);
        if (user == null) {
            return 0;
        }

        if(passwordEncoder.matches(password, user.getPassword())) {
            return user.getUserIdx();
        } else {
            return 0;
        }
    }

    /*
     * get user count
     * @param Map<String, Object>
     * return long
     */
    @Transactional(readOnly = true)
    public long getUserCount(Map<String, Object> param)
    {
        return userMapper.getUserCount(param);
    }

    /*
     * get user list
     * @param Map<String, Object>
     * return List<UserDto>
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUserList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return userMapper.getUserList(param);
    }

    /*
     * get all user list
     * return List<UserDto>
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUserList() {
        return userMapper.getAllUserList();
    }

    /*
     * insert goal user link
     * @param List<TeamUserLinkDto>
     * return int
     */
    @Transactional
    public int insertTeamUserLink(List<TeamUserLinkDto> list) {
        return userMapper.insertTeamUserLink(list);
    }

    /*
     * insert user link by team idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertUserLinkByUserIdx(long userIdx, List<Long> teamIdxList) {
        List<TeamUserLinkDto> valueList = new ArrayList<>();
        List<Long> teamUserList = new ArrayList<>(getTeamListByUserIdx(userIdx).stream()
                .map(TeamDto::getTeamIdx)
                .toList());

        // 값 리스트
        for (long teamIdx : teamIdxList) {
            if (teamUserList.contains(teamIdx)) {
                continue;
            }

            TeamUserLinkDto teamUserLinkDto = TeamUserLinkDto.builder()
                    .userIdx(userIdx)
                    .teamIdx(teamIdx)
                    .build();

            valueList.add(teamUserLinkDto);
            teamUserList.add(teamIdx);
        }

        return insertTeamUserLink(valueList);
    }

    /*
     * insert team link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertUserLinkByTeamIdx(long teamIdx, List<Long> userIdxList) {
        List<TeamUserLinkDto> valueList = new ArrayList<>();
        List<Long> teamUserList = new ArrayList<>(getUserListByTeamIdx(teamIdx).stream()
                .map(UserDto::getUserIdx)
                .toList());

        // 값 리스트
        for (long userIdx : userIdxList) {
            if (teamUserList.contains(userIdx)) {
                continue;
            }

            TeamUserLinkDto teamUserLinkDto = TeamUserLinkDto.builder()
                    .userIdx(userIdx)
                    .teamIdx(teamIdx)
                    .build();

            valueList.add(teamUserLinkDto);
            teamUserList.add(userIdx);
        }

        return insertTeamUserLink(valueList);
    }

    /*
     * delete team user link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int deleteTeamUserLinkByUserIdx(long userIdx, List<Long> deleteTeamIdxList) {
        Map<String, Object> param = new HashMap<>();
        param.put("user_idx", userIdx);
        param.put("team_idx_list", deleteTeamIdxList);

        return userMapper.deleteTeamUserLinkByUserIdx(param);
    }

    /*
     * delete team user link by team idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int deleteTeamUserLinkByTeamIdx(long teamIdx, List<Long> deleteUserIdxList) {
        Map<String, Object> param = new HashMap<>();
        param.put("team_idx", teamIdx);
        param.put("user_idx_list", deleteUserIdxList);

        return userMapper.deleteTeamUserLinkByTeamIdx(param);
    }
}
