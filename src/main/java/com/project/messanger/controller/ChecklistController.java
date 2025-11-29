package com.project.messanger.controller;

import com.project.messanger.dto.ChecklistDto;
import com.project.messanger.dto.ChecklistItemDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.ChecklistService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class ChecklistController {
    private final ChecklistService checklistService;
    private final AuthUtil authUtil;

    public ChecklistController(ChecklistService checklistService, AuthUtil authUtil) {
        this.checklistService = checklistService;
        this.authUtil = authUtil;
    }

    @PostMapping("/create")
    public Map<String, Object> insertChecklist(HttpServletRequest request,
                                               @RequestParam(value = "title", required = false) String title,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "item_title", required = false) List<String> itemTitleList,
                                               @RequestParam(value = "item_description", required = false) List<String> itemDescriptionList){
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

            checklistService.insertChecklistItem(checklistIdx, itemTitleList, itemDescriptionList);

            result.put("success", true);
            result.put("idx", checklistIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
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
                                               @RequestParam(value = "item_description", required = false) List<String> itemDescriptionList){
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
}
