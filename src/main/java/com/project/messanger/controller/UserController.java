package com.project.messanger.controller;

import com.project.messanger.dto.*;
import com.project.messanger.service.UserService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
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
                                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
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
            // 파라미터 세팅
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("email", email);
            param.put("user_name", userName);
            param.put("phone_number", phoneNumber.replaceAll("[^0-9]", ""));

            // 회원 목록 조회
            List<UserDto> userList = userService.getUserList(param);
            boolean isEmpty = userList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", userList);
                result.put("count", userService.getUserCount(param));
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "회원 정보를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/list/all")
    public Map<String, Object> getAllUserList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "회원 목록을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            // 전체 회원 목록 조회
            List<UserDto> userList = userService.getAllUserList();
            boolean isEmpty = userList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", userList);
            }
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
            UserDto userDto = userService.getUserByIdx(userIdx);

            result.put("success", userDto != null);
            if (userDto == null) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                // 유저가 소속된 팀 목록 조회
                List<TeamDto> teamDtoList = userService.getTeamListByUserIdx(userDto.getUserIdx());

                result.put("detail", userDto);
                result.put("team_list", teamDtoList);
            }
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
            // 전화번호 포맷
            if (phoneNumber != null)
            {
                phoneNumber = phoneNumber.replaceAll("[^0-9]", "").replaceAll("^(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
            }

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
            int success = userService.insertUser(userDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "회원을 등록하는데 실패했습니다.");
                return result;
            }

            long userIdx = userDto.getUserIdx();
            result.put("idx", userIdx);

            // 회원 팀 추가
            if (teamIdxList != null) {
                userService.insertUserLinkByUserIdx(userIdx, teamIdxList);
            }
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
                                          @RequestParam(value = "delete_team_idx", required = false) List<Long> deleteTeamIdxList) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "회원을 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // 전화번호 포맷
            if (phoneNumber != null)
            {
                phoneNumber = phoneNumber.replaceAll("[^0-9]", "").replaceAll("^(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
            }

            // 수정할 데이터 확인
            UserDto beforeData = userService.getUserByIdx(userIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "수정할 데이터가 없습니다.");

                return result;
            }

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
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "회원을 수정하는데 실패했습니다.");
                return result;
            }

            // 회원 팀 추가
            if (teamIdxList != null) {
                userService.insertUserLinkByUserIdx(userIdx, teamIdxList);
            }

            // 팀 삭제
            if (deleteTeamIdxList != null) {
                userService.deleteTeamUserLinkByUserIdx(userIdx, deleteTeamIdxList);
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
            // 삭제할 데이터 확인
            UserDto beforeData = userService.getUserByIdx(userIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "삭제할 데이터가 없습니다.");

                return result;
            }

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

    @PostMapping("/team/list")
    public Map<String, Object> getTeamList(HttpServletRequest request,
                                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                           @RequestParam(value = "team_name", required = false) String teamName) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            // 파라미터 세팅
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("team_name", teamName);

            // 팀 목록 조회
            List<TeamDto> teamList = userService.getTeamList(param);
            boolean isEmpty = teamList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", teamList);
                result.put("count", userService.getTeamCount(param));
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "팀 목록을 조회하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/team/list/all")
    public Map<String, Object> getAllTeamList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            // 팀 목록 조회
            List<TeamDto> teamList = userService.getAllTeamList();
            boolean isEmpty = teamList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", teamList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "팀 목록을 조회하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/team/detail")
    public Map<String, Object> getTeamDetail(HttpServletRequest request,
                                             @RequestParam("team_idx") long teamIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 조회할 권한이 없습니다.");

            return result;
        }

        try {
            // 팀 조회
            TeamDto teamDto = userService.getTeamByIdx(teamIdx);

            result.put("success", teamDto != null);
            if (teamDto == null) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                // 팀에 소속된 유저 목록 조회
                List<TeamUserLinkWithUserDto> userList = userService.getUserListByTeamIdx(teamIdx);

                result.put("detail", teamDto);
                result.put("user_list", userList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "팀 목록을 조회하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/team/create")
    public Map<String, Object> teamCreate(HttpServletRequest request,
                                          @RequestParam(value = "team_name", required = false) String teamName,
                                          @RequestParam(value = "user_idx", required = false) List<Long> userIdxList) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 등록할 권한이 없습니다.");

            return result;
        }

        try {
            // TeamDto 세팅
            TeamDto teamDto = TeamDto.builder()
                    .teamName(teamName)
                    .build();

            // 팀 등록
            int success = userService.insertTeam(teamDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "팀을 등록하는데 실패했습니다.");
                return result;
            }

            long teamIdx = teamDto.getTeamIdx();
            result.put("idx", teamIdx);

            // 팀 회원 추가
            if (userIdxList != null) {
                userService.insertUserLinkByTeamIdx(teamIdx, userIdxList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "팀을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/team/modify")
    public Map<String, Object> teamModify(HttpServletRequest request,
                                          @RequestParam("team_idx") long teamIdx,
                                          @RequestParam(value = "team_name", required = false) String teamName,
                                          @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                          @RequestParam(value = "delete_user_idx", required = false) List<Long> deleteUserIdxList) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "팀을 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // 수정할 데이터 확인
            TeamDto beforeData = userService.getTeamByIdx(teamIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "수정할 데이터가 없습니다.");

                return result;
            }

            // TeamDto 객체 생성
            TeamDto teamDto = TeamDto.builder()
                    .teamIdx(teamIdx)
                    .teamName(teamName)
                    .build();

            // 팀 수정
            int success = userService.updateTeam(teamDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "팀을 수정하는데 실패했습니다.");
                return result;
            }

            // 팀 회원 추가
            if (userIdxList != null) {
                userService.insertUserLinkByTeamIdx(teamIdx, userIdxList);
            }

            // 회원 삭제
            if (deleteUserIdxList != null) {
                userService.deleteTeamUserLinkByTeamIdx(teamIdx, deleteUserIdxList);
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
            // 삭제할 데이터 확인
            TeamDto beforeData = userService.getTeamByIdx(teamIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "삭제할 데이터가 없습니다.");

                return result;
            }

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
                List<Long> teamIdxList = new ArrayList<>(userService.getTeamListByUserIdx(userIdx).stream()
                        .map(TeamDto::getTeamIdx)
                        .toList());

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(userDto);
                String teamJsonData = objectMapper.writeValueAsString(teamIdxList);

                session.setAttribute("login_info", jsonData);
                session.setAttribute("team_idx_list", teamJsonData);

                result.put("login_info", userDto);
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
            // 전화번호 포맷
            if (phoneNumber != null)
            {
                phoneNumber = phoneNumber.replaceAll("[^0-9]", "").replaceAll("^(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
            }

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
