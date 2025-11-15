package com.project.messanger.controller;

import com.project.messanger.dto.UserDto;
import com.project.messanger.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
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

    @GetMapping("/list")
    public Map<String, Object> getUserList(HttpServletRequest request,
                                           @RequestParam("page") int page,
                                           @RequestParam("limit") int limit,
                                           @RequestParam("email") String email,
                                           @RequestParam("user_name") String userName,
                                           @RequestParam("phone_number") String phoneNumber) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 세션에서 로그인 데이터 꺼내서 권한 체크
        UserDto loginInfo = (UserDto)session.getAttribute("login_info");
        if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            result.put("success", false);
            result.put("error", "회원을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("email", email);
            param.put("user_name", userName);
            param.put("phone_number", phoneNumber);

            result.put("success", true);
            result.put("list", userService.getUserList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회원 정보를 불러올 수 없습니다.");
        }

        return result;
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
        HttpSession session = request.getSession();

        // 세션에서 로그인 데이터 꺼내서 권한 체크
        UserDto loginInfo = (UserDto)session.getAttribute("login_info");
        if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            result.put("success", false);
            result.put("error", "회원을 등록할 권한이 없습니다.");

            return result;
        }

        try {
            // UserDto 객체 생성
            UserDto userDto = UserDto.builder()
                    .email(email)
                    .password(password)
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .build();

            if (loginInfo.getAdminYn().equals("Y")) {
                userDto.setLeaderYn(leaderYn);
                userDto.setAdminYn(adminYn);
            }
            
            // 회원등록
            long userIdx = userService.insertUser(userDto);

            result.put("success", true);
            result.put("idx", userIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회원을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> modifyUser(HttpServletRequest request,
                                          @RequestParam("user_idx") long userIdx,
                                          @RequestParam("password") String password,
                                          @RequestParam("user_name") String userName,
                                          @RequestParam("phone_number") String phoneNumber,
                                          @RequestParam("admin_yn") String adminYn,
                                          @RequestParam("leader_yn") String leaderYn) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 세션에서 로그인 데이터 꺼내서 권한 체크
        UserDto loginInfo = (UserDto)session.getAttribute("login_info");
        if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            result.put("success", false);
            result.put("error", "회원을 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // UserDto 객체 생성
            UserDto userDto = UserDto.builder()
                    .userIdx(userIdx)
                    .password(password)
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .build();

            if (loginInfo.getAdminYn().equals("Y")) {
                userDto.setLeaderYn(leaderYn);
                userDto.setAdminYn(adminYn);
            }

            // 회원 정보 수정
            int success = userService.updateUser(userDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "회원을 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회원을 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteUser(HttpServletRequest request,
                                          @RequestParam("user_idx") long userIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 세션에서 로그인 데이터 꺼내서 권한 체크
        UserDto loginInfo = (UserDto)session.getAttribute("login_info");
        if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            result.put("success", false);
            result.put("error", "회원을 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 회원 삭제
            int success = userService.deleteUser(userIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "회원을 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회원을 삭제하는데 실패했습니다.");
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
            // 아이디, 비밀번호 확인
            long userIdx = userService.getLoginUserIdx(email, password);

            result.put("success", userIdx != 0);
            if (userIdx == 0) {
                result.put("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            } else {
                // 로그인 정보 저장
                UserDto userDto = userService.getUserByIdx(userIdx);
                session.setAttribute("login_info", userDto);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "로그인 중 오류가 발생했습니다.");
        }

        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> userLogout(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        session.removeAttribute("login_info");

        result.put("success", true);
        return result;
    }

    @PostMapping("/edit")
    public Map<String, Object> modifyMyInfo(HttpServletRequest request,
                                            @RequestParam("password") String password,
                                            @RequestParam("user_name") String userName,
                                            @RequestParam("phone_number") String phoneNumber) {
        HashMap<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();
        
        // 로그인 정보 확인
        UserDto loginInfo = (UserDto) session.getAttribute("login_info");
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // UserDto 객체 생성
            UserDto userDto = UserDto.builder()
                    .userIdx(loginInfo.getUserIdx())
                    .password(password)
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .build();

            // 내 정보 수정
            int success = userService.updateUser(userDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "내 정보를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "내 정보를 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/edit/password")
    public Map<String, Object> editPassword(HttpServletRequest request,
                                            @RequestParam("password") String password) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 로그인 정보 확인
        UserDto loginInfo = (UserDto) session.getAttribute("login_info");
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }
        
        try {
            // UserDto 객체 생성
            UserDto userDto = UserDto.builder()
                    .userIdx(loginInfo.getUserIdx())
                    .password(password)
                    .build();

            // 내 비밀번호 수정
            int success = userService.updateUser(userDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "내 비밀번호를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "내 비밀번호를 수정하는데 실패했습니다.");
        }

        return result;
    }
}
