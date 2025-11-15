package com.project.messanger.mapper;

import com.project.messanger.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    List<UserDto> getUserList(Map<String, Object> param);

    UserDto getUserByIdx(long userIdx);

    UserDto getUserByEmail(String email);

    long insertUser(UserDto userDto);

    int updateUser(UserDto userDto);

    int deleteUser(long userIdx);
}
