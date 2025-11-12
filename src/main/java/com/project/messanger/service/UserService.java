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
        if (userDto.getPassword() != null)
        {
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
        if (userDto.getPassword() != null)
        {
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
        }

        return userMapper.updateUser(userDto);
    }

    /*
     * get login user idx
     * @param String, String
     * return long
     */
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
}
