package com.project.messanger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.messanger.dto.TeamDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.UserService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AuthUtil authUtil;

    public UserController(UserService userService, AuthUtil authUtil) {
        this.userService = userService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getUserList(HttpServletRequest request,
                                           @RequestParam(value = "page", required = false) int page,
                                           @RequestParam(value = "limit", required = false) int limit,
                                           @RequestParam(value = "email", required = false) String email,
                                           @RequestParam(value = "user_name", required = false) String userName,
                                           @RequestParam(value = "phone_number", required = false) String phoneNumber) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "회원 목록을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
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

    @PostMapping("/detail")
    public Map<String, Object> getUserDetail(HttpServletRequest request,
                                             @RequestParam("user_idx") long userIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "회원을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            result.put("success", true);
            result.put("detail", userService.getUserByIdx(userIdx));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회원 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> createUser(HttpServletRequest request,
                                          @RequestParam("email") String email,
                                          @RequestParam(value = "password", required = false) String password,
                                          @RequestParam(value = "user_name", required = false) String userName,
                                          @RequestParam(value = "phone_number", required = false) String phoneNumber,
                                          @RequestParam(value = "admin_yn", required = false, defaultValue = "N") String adminYn,
                                          @RequestParam(value = "leader_yn", required = false, defaultValue = "N") String leaderYn,
                                          @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "회원을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            // 이메일 중복 체크
            if (userService.getUserByEmail(email) != null) {
                result.put("success", false);
                result.put("error", "이미 동일한 이메일이 등록되어 있습니다.");

                return result;
            }

            // UserDto 객체 생성
            UserDto userDto = UserDto.builder()
                    .email(email)
                    .password(password)
                    .userName(userName)
                    .phoneNumber(phoneNumber)
                    .build();

            // 관리자만 설정 가능
            UserDto loginInfo = authUtil.getLoginInfo(session);
            if (loginInfo.getAdminYn().equals("Y")) {
                userDto.setLeaderYn(leaderYn);
                userDto.setAdminYn(adminYn);
            }
            
            // 회원등록
            long userIdx = userService.insertUser(userDto);

            // 회원 팀 추가
            if (teamIdxList != null) {
                userService.insertUserLinkByTeamIdx(userIdx, teamIdxList);
            }

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
                                          @RequestParam(value = "password", required = false) String password,
                                          @RequestParam(value = "user_name", required = false) String userName,
                                          @RequestParam(value = "phone_number", required = false) String phoneNumber,
                                          @RequestParam(value = "admin_yn", required = false, defaultValue = "N") String adminYn,
                                          @RequestParam(value = "leader_yn", required = false, defaultValue = "N") String leaderYn,
                                          @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList,
                                          @RequestParam(value = "delete_link_idx", required = false) List<Long> deleteLinkIdxList) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
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

            // 관리자만 설정 가능
            UserDto loginInfo = authUtil.getLoginInfo(session);
            if (loginInfo.getAdminYn().equals("Y")) {
                userDto.setLeaderYn(leaderYn);
                userDto.setAdminYn(adminYn);
            }

            // 회원 정보 수정
            int success = userService.updateUser(userDto);

            // 회원 팀 추가
            if (teamIdxList != null) {
                userService.insertUserLinkByTeamIdx(userIdx, teamIdxList);
            }

            // 회원 삭제
            if (deleteLinkIdxList != null) {
                userService.deleteTeamUserLink(deleteLinkIdxList);
            }

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

        // 권한 체크
        if (!authUtil.authCheck(session)) {
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

    @PostMapping("/team/create")
    public Map<String, Object> teamCreate(HttpServletRequest request,
                                          @RequestParam(value = "team_name", required = false) String teamName) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 등록할 권한이 없습니다.");

            return result;
        }

        try {
            // 팀 등록
            long teamIdx = userService.insertTeam(teamName);

            result.put("success", true);
            result.put("idx", teamIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "팀을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/team/modify")
    public Map<String, Object> teamModify(HttpServletRequest request,
                                          @RequestParam("team_idx") long teamIdx,
                                          @RequestParam(value = "team_name", required = false) String teamName) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // TeamDto 객체 생성
            TeamDto teamDto = TeamDto.builder()
                    .teamIdx(teamIdx)
                    .teamName(teamName)
                    .build();

            // 팀 수정
            long success = userService.updateTeam(teamDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "팀을 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "팀을 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/team/delete")
    public Map<String, Object> deleteTeam(HttpServletRequest request,
                                          @RequestParam("team_idx") long teamIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 팀 삭제
            int success = userService.deleteTeam(teamIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "팀을 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "팀을 삭제하는데 실패했습니다.");
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
                List<Long> teamIdxList = userService.getTeamListByUserIdx(userIdx);

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(userDto);
                String teamJsonData = objectMapper.writeValueAsString(teamIdxList);

                session.setAttribute("login_info", jsonData);
                session.setAttribute("team_idx_list", teamJsonData);
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
                                            @RequestParam(value = "password", required = false) String password,
                                            @RequestParam("user_name") String userName,
                                            @RequestParam("phone_number") String phoneNumber) {
        HashMap<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            // 로그인 정보 확인
            UserDto loginInfo = authUtil.getLoginInfo(session);
            if (loginInfo == null) {
                result.put("success", false);
                result.put("error", "로그인 해주세요.");

                return result;
            }

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
        UserDto loginInfo = authUtil.getLoginInfo(session);
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
