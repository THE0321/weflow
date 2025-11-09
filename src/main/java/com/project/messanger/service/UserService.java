package com.project.messanger.service;

import com.project.messanger.dto.UserDto;
import com.project.messanger.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserDto getUserByIdx(long userIdx) {
        return userMapper.getUserByIdx(userIdx);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Transactional
    public long insertUser(UserDto userDto) {
        if (userDto.getPassword() != null)
        {
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
        }

        return userMapper.insertUser(userDto);
    }

    @Transactional
    public int updateUser(UserDto userDto) {
        if (userDto.getPassword() != null)
        {
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
        }

        return userMapper.updateUser(userDto);
    }
}
