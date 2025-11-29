package com.project.messanger.controller;

import com.project.messanger.dto.*;
import com.project.messanger.service.GoalService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;
    private final AuthUtil authUtil;

    public GoalController(GoalService goalService, AuthUtil authUtil) {
        this.goalService = goalService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getGoalList(HttpServletRequest request,
                                           @RequestParam(value = "page", required = false) int page,
                                           @RequestParam(value = "limit", required = false) int limit,
                                           @RequestParam(value = "title", required = false) String title,
                                           @RequestParam(value = "status", required = false) String status,
                                           @RequestParam(value = "my", required = false) boolean my) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            my = true;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("title", title);
            param.put("status", status);

            if (my) {
                param.put("user_idx", authUtil.getLoginInfo(session).getUserIdx());
                param.put("team_idx_list", authUtil.getTeamList(session));
            }

            result.put("success", true);
            result.put("list", goalService.getGoalList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "목표를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/detail")
    public Map<String, Object> getGoalDetail(HttpServletRequest request,
                                             @RequestParam("goal_idx") long goalIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            GoalAndLogDto goalAndLogDto = goalService.getGoalByIdx(goalIdx);
            if(goalAndLogDto == null) {
                result.put("success", false);
                result.put("error", "목표 상세를 불러올 수 없습니다.");

                return result;
            }

            List<GoalUserLinkDto> goalUserLinkList = goalService.getGoalUserLink(goalIdx);
            if (goalUserLinkList == null) {
                result.put("success", false);
                result.put("error", "목표 상세를 불러올 수 없습니다.");

                return result;
            } else {
                UserDto loginInfo = authUtil.getLoginInfo(session);
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (GoalUserLinkDto goalUserLinkDto : goalUserLinkList) {
                    if (goalUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(goalUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "목표 상세를 불러올 수 없습니다.");

                    return result;
                }
            }

            result.put("success", true);
            result.put("detail", goalAndLogDto);
            result.put("user_link_list", goalUserLinkList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "목표 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> saveGoal(HttpServletRequest request,
                                        @RequestParam(value = "title", required = false) String title,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam(value = "status", required = false) String status,
                                        @RequestParam(value = "target_value", required = false) long targetValue,
                                        @RequestParam(value = "start_date", required = false) String startDate,
                                        @RequestParam(value = "end_date", required = false) String endDate,
                                        @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                        @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "목표를 등록할 권한이 없습니다.");

            return result;
        }

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            // GoalDto 객체 생성
            GoalDto goalDto = GoalDto.builder()
                    .title(title)
                    .description(description)
                    .status(status)
                    .targetValue(targetValue)
                    .startDate(startDate)
                    .endDate(endDate)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            long goalIdx = goalService.insertGoal(goalDto);

            // 담당자 팀 추가
            if (teamIdxList != null) {
                goalService.insertGoalUserLinkByTeamIdx(goalIdx, teamIdxList);
            }

            // 담당자 유저 추가
            if (userIdxList != null) {
                goalService.insertGoalUserLinkByUserIdx(goalIdx, userIdxList);
            }

            result.put("success", true);
            result.put("idx", goalIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "목표를 등록할 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateGoal(HttpServletRequest request,
                                          @RequestParam(value = "goal_idx") Long goalIdx,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam(value = "status", required = false) String status,
                                          @RequestParam(value = "target_value", required = false) long targetValue,
                                          @RequestParam(value = "start_date", required = false) String startDate,
                                          @RequestParam(value = "end_date", required = false) String endDate,
                                          @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                          @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList,
                                          @RequestParam(value = "delete_link_idx", required = false) List<Long> deleteLinkIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "목표를 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // GoalDto 객체 생성
            GoalDto goalDto = GoalDto.builder()
                    .title(title)
                    .description(description)
                    .status(status)
                    .targetValue(targetValue)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            // 목표 수정
            int success = goalService.updateGoal(goalDto);

            // 담당자 팀 추가
            if (teamIdxList != null) {
                goalService.insertGoalUserLinkByTeamIdx(goalIdx, teamIdxList);
            }

            // 담당자 유저 추가
            if (userIdxList != null) {
                goalService.insertGoalUserLinkByUserIdx(goalIdx, userIdxList);
            }

            // 담당자 삭제
            if (deleteLinkIdxList != null) {
                goalService.deleteGoalUserLink(deleteLinkIdxList);
            }

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "목표를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "목표를 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteGoal(HttpServletRequest request,
                                          @RequestParam("goal_idx") long goalIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "목표를 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 회원 삭제
            int success = goalService.deleteGoal(goalIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "목표를 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "목표를 삭제하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/log/list")
    public Map<String, Object> getGoalLogList(HttpServletRequest request,
                                              @RequestParam(value = "goal_idx", required = false) long goalIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            List<GoalUserLinkDto> goalUserLinkList = goalService.getGoalUserLink(goalIdx);
            if (goalUserLinkList == null) {
                result.put("success", false);
                result.put("error", "실적을 조회할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (GoalUserLinkDto goalUserLinkDto : goalUserLinkList) {
                    if (goalUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(goalUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "실적을 조회할 수 없습니다.");

                    return result;
                }
            }

            result.put("success", true);
            result.put("list", goalService.getGoalLog(goalIdx));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "실적을 조회할 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/log/create")
    public Map<String, Object> saveGoalLog(HttpServletRequest request,
                                           @RequestParam(value = "goal_idx", required = false) long goalIdx,
                                           @RequestParam(value = "progress_value", required = false) long progressValue,
                                           @RequestParam(value = "content", required = false) String content
                                        ){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            List<GoalUserLinkDto> goalUserLinkList = goalService.getGoalUserLink(goalIdx);
            if (goalUserLinkList == null) {
                result.put("success", false);
                result.put("error", "실적을 등록할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (GoalUserLinkDto goalUserLinkDto : goalUserLinkList) {
                    if (goalUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(goalUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "실적을 등록할 수 없습니다.");

                    return result;
                }
            }

            // GoalLogDto 객체 생성
            GoalLogDto goalLogDto = GoalLogDto.builder()
                    .goalIdx(goalIdx)
                    .progressValue(progressValue)
                    .content(content)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            long logIdx = goalService.insertGoalLog(goalLogDto);
            if(logIdx == 0) {
                result.put("success", false);
                result.put("error", "실적이 등록되지 않았습니다.");

                return result;
            }

            result.put("success", true);
            result.put("idx", logIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "실적이 등록되지 않았습니다.");
        }

        return result;
    }

    @PostMapping("/log/modify")
    public Map<String, Object> updateGoalLog(HttpServletRequest request,
                                             @RequestParam(value = "goal_idx") long goalIdx,
                                             @RequestParam(value = "log_idx") long logIdx,
                                             @RequestParam(value = "content", required = false) String content,
                                             @RequestParam(value = "progress_value", required = false) long progressValue){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            List<GoalUserLinkDto> goalUserLinkList = goalService.getGoalUserLink(goalIdx);
            if (goalUserLinkList == null) {
                result.put("success", false);
                result.put("error", "실적을 수정할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (GoalUserLinkDto goalUserLinkDto : goalUserLinkList) {
                    if (goalUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(goalUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "실적을 수정할 수 없습니다.");

                    return result;
                }
            }

            List<GoalLogDto> goalLogList = goalService.getGoalLog(goalIdx);
            if (goalLogList.getFirst().getLogIdx() == logIdx) {
                result.put("success", false);
                result.put("error", "실적을 수정할 수 없습니다.");

                return result;
            }

            // GoalLogDto 객체 생성
            GoalLogDto goalLogDto = GoalLogDto.builder()
                        .logIdx(logIdx)
                        .progressValue(progressValue)
                        .content(content)
                        .build();

            // 실적 수정
            int success = goalService.updateGoalLog(goalLogDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "실적을 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "실적을 삭제하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/log/delete")
    public Map<String, Object> deleteGoalLog(HttpServletRequest request,
                                             @RequestParam("goal_idx") long goalIdx,
                                             @RequestParam("log_idx") long logIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            List<GoalUserLinkDto> goalUserLinkList = goalService.getGoalUserLink(goalIdx);
            if (goalUserLinkList == null) {
                result.put("success", false);
                result.put("error", "실적을 삭제할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (GoalUserLinkDto goalUserLinkDto : goalUserLinkList) {
                    if (goalUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(goalUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "실적을 삭제할 수 없습니다.");

                    return result;
                }
            }

            List<GoalLogDto> goalLogList = goalService.getGoalLog(goalIdx);
            if (goalLogList.getFirst().getLogIdx() == logIdx) {
                result.put("success", false);
                result.put("error", "실적을 삭제할 수 없습니다.");

                return result;
            }

            // 실적 삭제
            int success = goalService.deleteGoalLog(logIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "실적을 삭제하는데 실패했습니다.");
            }

            result.put("success", true);
            result.put("idx", success);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "실적을 삭제하는데 실패했습니다.");
        }

        return result;
    }
}
