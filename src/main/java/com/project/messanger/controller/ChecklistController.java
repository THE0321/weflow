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
                                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                                @RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(value = "my", required = false) boolean my) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            my = true;
        }

        try {
            // 파라미터 세팅
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("title", title);
            param.put("status", status);

            if (my) {
                param.put("user_idx", loginInfo.getUserIdx());
                param.put("team_idx_list", authUtil.getTeamList(session));
            }

            // 체크리스트 목록 조회
            List<ChecklistWithUserDto> checklistList = checklistService.getChecklistList(param);
            boolean isEmpty = checklistList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", checklistList);
                result.put("count", checklistService.getChecklistCount(param));
            }
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

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("user_idx", loginInfo.getUserIdx());

            List<Long> teamIdxList = authUtil.getTeamList(session);
            if (!teamIdxList.isEmpty()) {
                param.put("team_idx_list", teamIdxList);
            }

            // 체크리스트 메인 목록 조회
            List<ChecklistWithUserDto> checklistList = checklistService.getChecklistMainList(param);
            boolean isEmpty = checklistList.isEmpty();

            result.put("success", !isEmpty);
            if (isEmpty) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", checklistList);
            }
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

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 체크리스트 조회
            ChecklistDto checklistDto = checklistService.getChecklistByIdx(checklistIdx);
            if(checklistDto == null) {
                result.put("success", false);
                result.put("error", "체크리스트 상세를 불러올 수 없습니다.");

                return result;
            }

            // 체크리스트 상세 조회
            List<ChecklistUserLinkWithUserDto> goalUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (goalUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 상세를 불러올 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkWithUserDto checklistUserLinkDto : goalUserLinkList) {
                    if (checklistUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(checklistUserLinkDto.getTeamIdx())) {
                        isMyGoal = true;
                        break;
                    }
                }

                if (!isMyGoal) {
                    result.put("success", false);
                    result.put("error", "체크리스트 상세를 불러올 수 없습니다.");

                    return result;
                }
            }

            result.put("success", true);
            result.put("detail", checklistDto);
            result.put("item_list", checklistService.getChecklistItemList(checklistIdx));
            result.put("user_link_list", goalUserLinkList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertChecklist(HttpServletRequest request,
                                               @RequestParam(value = "title") String title,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "item_title", required = false) List<String> itemTitleList,
                                               @RequestParam(value = "item_description", required = false) List<String> itemDescriptionList,
                                               @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                               @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        // 권한 체크
        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            result.put("success", false);
            result.put("error", "체크리스트를 등록할 권한이 없습니다.");

            return result;
        }

        try {
            // ChecklistDto 객체 생성
            ChecklistDto checklistDto = ChecklistDto.builder()
                        .title(title)
                        .description(description)
                        .status(status)
                        .creatorIdx(loginInfo.getUserIdx())
                        .build();

            // 체크리스트 등록
            long checklistIdx = checklistService.insertChecklist(checklistDto);

            // 체크리스트 항목 등록
            if (itemTitleList == null)
            {
                result.put("success", false);
                result.put("error", "체크리스트를 등록할 수 없습니다.");

                return result;
            }

            checklistService.insertChecklistItem(checklistIdx, itemTitleList, itemDescriptionList);

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

            result.put("success", true);
            result.put("idx", checklistIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "체크리스트를 등록할 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateChecklist(HttpServletRequest request,
                                               @RequestParam(value = "checklist_idx") Long checklistIdx,
                                               @RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "item_idx", required = false) List<Long> itemIdxList,
                                               @RequestParam(value = "item_title", required = false) List<String> itemTitleList,
                                               @RequestParam(value = "item_description", required = false) List<String> itemDescriptionList,
                                               @RequestParam(value = "delete_item_idx", required = false) List<Long> deleteItemIdxList,
                                               @RequestParam(value = "user_idx", required = false) List<Long> userIdxList,
                                               @RequestParam(value = "team_idx", required = false) List<Long> teamIdxList,
                                               @RequestParam(value = "delete_user_idx", required = false) List<Long> deleteUserIdxList,
                                               @RequestParam(value = "delete_team_idx", required = false) List<Long> deleteTeamIdxList){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "체크리스트를 수정할 권한이 없습니다.");

            return result;
        }

        try {
            // 수정할 데이터 확인
            ChecklistDto beforeData = checklistService.getChecklistByIdx(checklistIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "수정할 데이터가 없습니다.");

                return result;
            }

            // ChecklistDto 객체 생성
            ChecklistDto checklistDto = ChecklistDto.builder()
                    .checklistIdx(checklistIdx)
                    .title(title)
                    .description(description)
                    .status(status)
                    .build();

            // 체크리스트 수정
            int success = checklistService.updateChecklist(checklistDto);

            // 체크리스트 항목 수정
            if (itemTitleList != null)
            {
                List<Integer> updateIdxList = new ArrayList<>();
                for (int i = 0; i < itemTitleList.size(); i++) {
                    if (itemIdxList.get(i) == null) {
                        continue;
                    }

                    // ChecklistItemDto 세팅
                    ChecklistItemDto checklistItemDto = ChecklistItemDto.builder()
                            .itemIdx(itemIdxList.get(i))
                            .title(itemTitleList.get(i))
                            .description(itemDescriptionList.get(i))
                            .build();

                    checklistService.updateChecklistItem(checklistItemDto);
                    updateIdxList.add(i);
                }

                // 수정한 항목 목록 제거
                for (int idx : updateIdxList) {
                    itemIdxList.remove(idx);
                    itemTitleList.remove(idx);
                    itemDescriptionList.remove(idx);
                }

                // 체크리스트 항목 등록
                if (!itemTitleList.isEmpty()) {
                    checklistService.insertChecklistItem(checklistIdx, itemTitleList, itemDescriptionList);
                }
            }

            // 체크리스트 항목 삭제
            if (deleteItemIdxList != null) {
                checklistService.deleteChecklistItem(deleteItemIdxList);
            }

            // 담당자 유저 추가
            if (userIdxList != null) {
                checklistService.insertChecklistUserLinkByUserIdx(checklistIdx, userIdxList);
            }

            // 담당자 팀 추가
            if (teamIdxList != null) {
                checklistService.insertChecklistUserLinkByTeamIdx(checklistIdx, teamIdxList);
            }

            // 담당자 유저 삭제
            if (deleteUserIdxList != null) {
                checklistService.deleteChecklistUserLinkByUserIdx(checklistIdx, deleteUserIdxList);
            }

            // 담당자 팀 삭제
            if (deleteTeamIdxList != null) {
                checklistService.deleteChecklistUserLinkByTeamIdx(checklistIdx, deleteTeamIdxList);
            }

            result.put("success", success != 0);
            if (success == 0) {
                result.put("error", "체크리스트를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();

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

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        // 권한 체크
        if (!authUtil.authCheck(session)) {
            result.put("success", false);
            result.put("error", "체크리스트를 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 삭제할 데이터 확인
            ChecklistDto beforeData = checklistService.getChecklistByIdx(checklistIdx);
            if (beforeData == null) {
                result.put("success", false);
                result.put("error", "삭제할 데이터가 없습니다.");

                return result;
            }

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

    @PostMapping("/log/list")
    public Map<String, Object> getChecklistLogList(HttpServletRequest request,
                                                   @RequestParam(value = "checklist_idx") long checklistIdx){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 체크리스트 담당자 확인
            List<ChecklistUserLinkWithUserDto> checklistUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (checklistUserLinkList == null) {
                result.put("success", false);
                result.put("error", "실적을 조회할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkWithUserDto checklistUserLinkDto : checklistUserLinkList) {
                    if (checklistUserLinkDto.getUserIdx() == loginInfo.getUserIdx() ||
                            teamIdxList.contains(checklistUserLinkDto.getTeamIdx())) {
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

            // 체크리스트 로그 조회
            List<ChecklistLogDto> checklistLogList = checklistService.getChecklistLog(checklistIdx);

            result.put("success", checklistLogList != null);
            if (checklistLogList == null) {
                result.put("error", "조회할 데이터가 없습니다.");
            } else {
                result.put("list", checklistLogList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "실적을 조회할 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/log/create")
    public Map<String, Object> insertChecklistLog(HttpServletRequest request,
                                                  @RequestParam(value = "checklist_idx") long checklistIdx,
                                                  @RequestParam(value = "item_idx") long itemIdx,
                                                  @RequestParam(value = "is_checked", required = false) String isChecked,
                                                  @RequestParam(value = "content", required = false) String content){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 체크리스트 담당자 확인
            List<ChecklistUserLinkWithUserDto> checklistUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (checklistUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 등록할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkWithUserDto checklistUserLinkDto : checklistUserLinkList) {
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
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            // 체크리스트 등록
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
                                                  @RequestParam(value = "is_checked", required = false) String isChecked,
                                                  @RequestParam(value = "content", required = false) String content){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 체크리스트 담당자 확인
            List<ChecklistUserLinkWithUserDto> checklistUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (checklistUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 수정할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkWithUserDto checklistUserLinkDto : checklistUserLinkList) {
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

            // 수정할 데이터 확인
            List<ChecklistLogDto> checklistLogList = checklistService.getChecklistLog(itemIdx);
            if (checklistLogList.getFirst().getLogIdx() == logIdx) {
                System.out.println("3");
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

            result.put("success", success != 0);
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
                                                  @RequestParam(value = "log_idx") long logIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 체크리스트 담당자 확인
            List<ChecklistUserLinkWithUserDto> checklistUserLinkList = checklistService.getChecklistUserLink(checklistIdx);
            if (checklistUserLinkList == null) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 삭제할 수 없습니다.");

                return result;
            } else if (loginInfo.getLeaderYn().equals("N") && loginInfo.getAdminYn().equals("N")) {
                List<Long> teamIdxList = authUtil.getTeamList(session);
                boolean isMyGoal = false;
                for (ChecklistUserLinkWithUserDto checklistUserLinkDto : checklistUserLinkList) {
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

            // 삭제할 데이터 확인
            List<ChecklistLogDto> goalLogList = checklistService.getChecklistLog(itemIdx);
            if (goalLogList.getFirst().getLogIdx() == logIdx) {
                result.put("success", false);
                result.put("error", "체크리스트 결과를 삭제할 수 없습니다.");

                return result;
            }

            // 체크리스트 결과 삭제
            int success = checklistService.deleteChecklistLog(logIdx);

            result.put("success", success != 0);
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
