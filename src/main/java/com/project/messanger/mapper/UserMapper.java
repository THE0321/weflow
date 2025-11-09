package com.project.messanger.mapper;

import com.project.messanger.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserDto getUserByIdx(long userIdx);

    UserDto getUserByEmail(String email);

    long insertUser(UserDto userDto);

    int updateUser(UserDto userDto);
}
