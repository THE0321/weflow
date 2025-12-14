package com.project.messanger.controller;

import com.project.messanger.dto.*;
import com.project.messanger.service.ChecklistService;
import com.project.messanger.service.NotificationService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class ChecklistController {
    private final ChecklistService checklistService;
    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    public ChecklistController(ChecklistService checklistService, NotificationService notificationService, AuthUtil authUtil) {
        this.checklistService = checklistService;
        this.notificationService = notificationService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getChecklistList(HttpServletRequest request,
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
            result.put("list", checklistService.getChecklistList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/main/list")
    public Map<String, Object> getChecklistList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("user_idx", authUtil.getLoginInfo(session).getUserIdx());
            param.put("team_idx_list", authUtil.getTeamList(session));

            result.put("success", true);
            result.put("list", checklistService.getChecklistMainList(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/detail")
    public Map<String, Object> getChecklistDetail(HttpServletRequest request,
                                                  @RequestParam(value = "checklist_idx") Long checklistIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            ChecklistDto checklistDto = checklistService.getChecklistByIdx(checklistIdx);
            if(checklistDto == null) {
                result.put("success", false);
                result.put("error", "체크리스트 상세를 불러올 수 없습니다.");

                return result;
            }

            List<ChecklistUserLinkDto> goalUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (goalUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 상세를 불러올 수 없습니다.");

                return result;
            } else {
                UserDto loginInfo = authUtil.getLoginInfo(session);
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkDto checklistUserLinkDto : goalUserLinkList) {
                    if (checklistUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(checklistUserLinkDto.getTeamIdx())) {
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
            result.put("detail", checklistDto);
            result.put("user_link_list", goalUserLinkList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "목표 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertChecklist(HttpServletRequest request,
                                               @RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "item_title", required = false) List<String> itemTitleList,
                                               @RequestParam(value = "item_description", required = false) List<String> itemDescriptionList,
                                               @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                               @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "체크리스트를 등록할 권한이 없습니다.");

            return result;
        }

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            // ChecklistDto 객체 생성
            ChecklistDto checklistDto = ChecklistDto.builder()
                        .title(title)
                        .description(description)
                        .status(status)
                        .creatorIdx(loginInfo.getUserIdx())
                        .build();

            long checklistIdx = checklistService.insertChecklist(checklistDto);

            NotificationDto notificationDto = NotificationDto.builder()
                    .type("CHECKLIST")
                    .content("체크리스트가 등록되었습니다.")
                    .linkUrl("/check/" + checklistIdx)
                    .build();

            // 담당자 팀 추가
            if (teamIdxList != null) {
                checklistService.insertChecklistUserLinkByTeamIdx(checklistIdx, teamIdxList);

                // 알림 등록
                notificationService.insertNotificationByTeamIdx(notificationDto, teamIdxList);
            }

            // 담당자 유저 추가
            if (userIdxList != null) {
                checklistService.insertChecklistUserLinkByUserIdx(checklistIdx, userIdxList);

                // 알림 등록
                notificationService.insertNotificationByUserIdx(notificationDto, userIdxList);
            }

            checklistService.insertChecklistItem(checklistIdx, itemTitleList, itemDescriptionList);

            result.put("success", true);
            result.put("idx", checklistIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트를 등록할 권한이 없습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateChecklist(HttpServletRequest request,
                                               @RequestParam(value = "checklist_idx") Long checklistIdx,
                                               @RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "item_title", required = false) List<String> itemTitleList,
                                               @RequestParam(value = "item_description", required = false) List<String> itemDescriptionList,
                                               @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                               @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList,
                                               @RequestParam(value = "delete_link_idx", required = false) List<Long> deleteLinkIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "체크리스트를 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // ChecklistDto 객체 생성
            ChecklistDto checklistDto = ChecklistDto.builder()
                    .checklistIdx(checklistIdx)
                    .title(title)
                    .description(description)
                    .status(status)
                    .build();

            // 체크리스트 수정
            int success = checklistService.updateChecklist(checklistDto);

            for (int i = 0; i < itemTitleList.size(); i++) {
                ChecklistItemDto checklistItemDto = ChecklistItemDto.builder()
                        .title(itemTitleList.get(i))
                        .description(itemDescriptionList.get(i))
                        .build();

                checklistService.updateChecklistItem(checklistItemDto);
            }

            // 담당자 팀 추가
            if (teamIdxList != null) {
                checklistService.insertChecklistUserLinkByTeamIdx(checklistIdx, teamIdxList);
            }

            // 담당자 유저 추가
            if (userIdxList != null) {
                checklistService.insertChecklistUserLinkByUserIdx(checklistIdx, userIdxList);
            }

            // 담당자 삭제
            if (deleteLinkIdxList != null) {
                checklistService.deleteChecklistUserLink(deleteLinkIdxList);
            }

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "체크리스트를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트를 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteChecklist(HttpServletRequest request,
                                               @RequestParam("checklist_idx") long checklistIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "체크리스트를 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 체크리스트 삭제
            int success = checklistService.deleteChecklist(checklistIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "체크리스트를 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트를 삭제하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/log/create")
    public Map<String, Object> insertChecklistLog(HttpServletRequest request,
                                                  @RequestParam(value = "checklist_idx", required = false) long checklistIdx,
                                                  @RequestParam(value = "item_idx", required = false) long itemIdx,
                                                  @RequestParam(value = "is_checked", required = false) char isChecked,
                                                  @RequestParam(value = "content", required = false) String content
    ){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            List<ChecklistUserLinkDto> checklistUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (checklistUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 등록할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkDto checklistUserLinkDto : checklistUserLinkList) {
                    if (checklistUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(checklistUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "체크리스트 결과를 등록할 수 없습니다.");

                    return result;
                }
            }

            // ChecklistLogDto 객체 생성
            ChecklistLogDto checklistLogDto = ChecklistLogDto.builder()
                    .itemIdx(itemIdx)
                    .isChecked(isChecked)
                    .content(content)
                    .build();

            long logIdx = checklistService.insertChecklistLog(checklistLogDto);
            if(logIdx == 0) {
                result.put("success", false);
                result.put("error", "체크리스트 결과가 등록되지 않았습니다.");

                return result;
            }

            result.put("success", true);
            result.put("idx", logIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트 결과가 등록되지 않았습니다.");
        }

        return result;
    }

    @PostMapping("/log/modify")
    public Map<String, Object> updateChecklistLog(HttpServletRequest request,
                                                  @RequestParam(value = "checklist_idx") long checklistIdx,
                                                  @RequestParam(value = "item_idx") long itemIdx,
                                                  @RequestParam(value = "log_idx") long logIdx,
                                                  @RequestParam(value = "is_checked", required = false) char isChecked,
                                                  @RequestParam(value = "content", required = false) String content){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            List<ChecklistUserLinkDto> checklistUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (checklistUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 수정할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkDto checklistUserLinkDto : checklistUserLinkList) {
                    if (checklistUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(checklistUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "체크리스트 결과를 수정할 수 없습니다.");

                    return result;
                }
            }

            List<ChecklistLogDto> checklistLogList = checklistService.getChecklistLog(itemIdx);
            if (checklistLogList.getFirst().getLogIdx() == logIdx) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 수정할 수 없습니다.");

                return result;
            }

            // ChecklistLogDto 객체 생성
            ChecklistLogDto checklistLogDto = ChecklistLogDto.builder()
                    .logIdx(logIdx)
                    .isChecked(isChecked)
                    .content(content)
                    .build();

            // 체크리스트 결과 수정
            int success = checklistService.updateChecklistLog(checklistLogDto);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "체크리스트 결과를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트 결과를 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/log/delete")
    public Map<String, Object> deleteChecklistLog(HttpServletRequest request,
                                                  @RequestParam(value = "checklist_idx") long checklistIdx,
                                                  @RequestParam(value = "item_idx") long itemIdx,
                                                  @RequestParam("log_idx") long logIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            UserDto loginInfo = authUtil.getLoginInfo(session);

            List<ChecklistUserLinkDto> checklistUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (checklistUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 삭제할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkDto checklistUserLinkDto : checklistUserLinkList) {
                    if (checklistUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(checklistUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "체크리스트 결과를 삭제할 수 없습니다.");

                    return result;
                }
            }

            List<ChecklistLogDto> goalLogList = checklistService.getChecklistLog(itemIdx);
            if (goalLogList.getFirst().getLogIdx() == logIdx) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 삭제할 수 없습니다.");

                return result;
            }

            // 체크리스트 결과 삭제
            int success = checklistService.deleteChecklistLog(logIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "체크리스트 결과를 삭제하는데 실패했습니다.");
            }

            result.put("success", true);
            result.put("idx", success);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트 결과를 삭제하는데 실패했습니다.");
        }

        return result;
    }
}
