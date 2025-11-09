package com.project.messanger.controller;

import com.project.messanger.dto.UserDto;
import com.project.messanger.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public Map<String, Object> createUser(HttpServletRequest request,
                                          @RequestParam("email") String email,
                                          @RequestParam("password") String password,
                                          @RequestParam("user_name") String userName,
                                          @RequestParam("phone_number") String phoneNumber,
                                          @RequestParam("admin_yn") String adminYn,
                                          @RequestParam("leader_yn") String leaderYn) {
        Map<String, Object> result = new HashMap<>();

        try {
            UserDto userDto = UserDto.builder()
                    .email(email)
                    .password(password)
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .adminYn(adminYn)
                    .leaderYn(leaderYn)
                    .build();
            long userIdx = userService.insertUser(userDto);

            result.put("success", "true");
            result.put("idx", userIdx);
        } catch (Exception e) {
            result.put("success", "false");
            result.put("error", "회원을 등록하는데 실패했습니다.");
        }

        return result;
    }
}
