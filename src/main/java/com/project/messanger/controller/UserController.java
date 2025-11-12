package com.project.messanger.controller;

import com.project.messanger.dto.UserDto;
import com.project.messanger.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @PostMapping("/login")
    public Map<String, Object> userLogin(HttpServletRequest request,
                                         @RequestParam("email") String email,
                                         @RequestParam("password") String password) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            long userIdx = userService.getLoginUserIdx(email, password);

            result.put("success", userIdx != 0);
            if(userIdx == 0) {
                result.put("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            } else {
                session.setAttribute("userIdx", userIdx);
            }

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "로그인 중 오류가 발생했습니다.");

            return result;
        }
    }
}
