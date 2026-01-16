package com.project.messanger.service;

import com.project.messanger.dto.*;
import com.project.messanger.mapper.ChecklistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChecklistService {
    private final ChecklistMapper checklistMapper;

    public ChecklistService(ChecklistMapper checklistMapper) {
        this.checklistMapper = checklistMapper;
    }

    /*
     * get checklist count
     * @param Map<String, Object>
     * return long
     */
    public long getChecklistCount(Map<String, Object> param) {
        return checklistMapper.getChecklistCount(param);
    }

    /*
     * get checklist list
     * @param Map<String, Object>
     * return List<ChecklistWithUserDto>
     */
    public List<ChecklistWithUserDto> getChecklistList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return checklistMapper.getChecklistList(param);
    }

    /*
     * get checklist main list
     * @param Map<String, Object>
     * return List<ChecklistWithUserDto>
     */
    public List<ChecklistWithUserDto> getChecklistMainList(Map<String, Object> param) {
        return checklistMapper.getChecklistMainList(param);
    }

    /*
     * get checklist by goal idx
     * @param long
     * return goalDto
     */
    @Transactional(readOnly = true)
    public ChecklistDto getChecklistByIdx(long checklistIdx) {
        return checklistMapper.getChecklistByIdx(checklistIdx);
    }

    /*
     * get checklist by goal idx
     * @param long
     * return List<ChecklistGraphDto>
     */
    @Transactional(readOnly = true)
    public List<ChecklistGraphDto> getChecklistGraph(Map<String, Object> param) {
        return checklistMapper.getChecklistGraph(param);
    }

    /*
     * get checklist list log
     * @param long
     * return List<ChecklistLogDto>
     */
    @Transactional(readOnly = true)
    public List<ChecklistLogDto> getChecklistLog(long itemIdx) {
        return checklistMapper.getChecklistLog(itemIdx);
    }

    /*
     * insert checklist
     * @param ChecklistDto
     * return int
     */
    @Transactional
    public int insertChecklist(ChecklistDto checklistDto) {
        return checklistMapper.insertChecklist(checklistDto);
    }

    /*
     * update checklist
     * @param ChecklistDto
     * return int
     */
    @Transactional
    public int updateChecklist(ChecklistDto checklistDto) {
        return checklistMapper.updateChecklist(checklistDto);
    }

    /*
     * delete checklist
     * @param long
     * return int
     */
    @Transactional
    public int deleteChecklist(long checklistIdx) {
        return checklistMapper.deleteChecklist(checklistIdx);
    }

    /*
     * get checklist item list
     * @param long
     * return List<ChecklistItemDto>
     */
    @Transactional(readOnly = true)
    public List<ChecklistItemDto> getChecklistItemList(long checklistIdx) {
        return checklistMapper.getChecklistItemList(checklistIdx);
    }

    /*
     * get checklist item by idx
     * @param long
     * return ChecklistItemDto
     */
    @Transactional(readOnly = true)
    public ChecklistItemDto getChecklistItemByIdx(long itemIdx) {
        return checklistMapper.getChecklistItemByIdx(itemIdx);
    }

    /*
     * insert checklist item
     * @param List<String>
     * return int
     */
    @Transactional
    public int insertChecklistItem(long checklistIdx, List<String> itemTitleList, List<String> itemDescriptionList) {
        List<ChecklistItemDto> valueList = new ArrayList<>();

        // 값 리스트
        for (int i = 0; i < itemTitleList.size(); i++) {
            ChecklistItemDto checklistItemDto = ChecklistItemDto.builder()
                    .checklistIdx(checklistIdx)
                    .title(itemTitleList.get(i))
                    .description(itemDescriptionList.get(i))
                    .build();
            valueList.add(checklistItemDto);
        }

        if (valueList.isEmpty()) {
            return 0;
        }

        return checklistMapper.insertChecklistItem(valueList);
    }

    /*
     * update checklist item
     * @param ChecklistItemDto
     * return int
     */
    @Transactional
    public int updateChecklistItem(ChecklistItemDto checklistItemDto) {
        return checklistMapper.updateChecklistItem(checklistItemDto);
    }

    /*
     * delete checklist item
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteChecklistItem(List<Long> deleteChecklistItemIdxList) {
        return checklistMapper.deleteChecklistItem(deleteChecklistItemIdxList);
    }

    /*
     * get checklist user link
     * @param long
     * return List<ChecklistUserLinkWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<ChecklistUserLinkWithUserDto> getChecklistUserLink(long checklistIdx) {
        return checklistMapper.getChecklistUserLink(checklistIdx);
    }

    /*
     * insert checklist user link
     * @param List<ChecklistUserLinkDto>
     * return int
     */
    @Transactional
    public int insertChecklistUserLink(List<ChecklistUserLinkDto> checklistUserLinkDtoList) {
        return checklistMapper.insertChecklistUserLink(checklistUserLinkDtoList);
    }

    /*
     * delete checklist user link by user idx
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteChecklistUserLinkByUserIdx(long checklistIdx, List<Long> deleteLinkIdxList) {
        Map<String, Object> param = new HashMap<>();
        param.put("checklist_idx", checklistIdx);
        param.put("user_idx_list", deleteLinkIdxList);

        return checklistMapper.deleteChecklistUserLinkByUserIdx(param);
    }

    /*
     * delete checklist user link by team idx
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteChecklistUserLinkByTeamIdx(long checklistIdx, List<Long> deleteLinkIdxList) {
        Map<String, Object> param = new HashMap<>();
        param.put("checklist_idx", checklistIdx);
        param.put("team_idx_list", deleteLinkIdxList);

        return checklistMapper.deleteChecklistUserLinkByTeamIdx(param);
    }

    /*
     * insert checklist log
     * @param ChecklistLogDto
     * return int
     */
    @Transactional
    public int insertChecklistLog(ChecklistLogDto checklistLogDto) {
        // 마지막 등록된 결과 조회
        if (checklistLogDto.getIsChecked() == null) {
            ChecklistLogWithUserDto lastData = checklistMapper.getChecklistLogLast(checklistLogDto.getItemIdx());
            if (lastData != null) {
                checklistLogDto.setIsChecked(lastData.getIsChecked());
            }
        }

        return checklistMapper.insertChecklistLog(checklistLogDto);
    }

    /*
     * update checklist log
     * @param ChecklistLogDto
     * return int
     */
    @Transactional
    public int updateChecklistLog(ChecklistLogDto checklistLogDto) {
        return checklistMapper.updateChecklistLog(checklistLogDto);
    }

    /*
     * delete checklist log
     * @param long
     * return int
     */
    @Transactional
    public int deleteChecklistLog(long logIdx) {
        return checklistMapper.deleteChecklistLog(logIdx);
    }

    /*
     * insert checklist user link by team idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertChecklistUserLinkByTeamIdx(long checklistIdx, List<Long> teamIdxList) {
        List<ChecklistUserLinkDto> valueList = new ArrayList<>();
        List<Long> checklistUserList = new ArrayList<>(getChecklistUserLink(checklistIdx).stream()
                .map(ChecklistUserLinkWithUserDto::getTeamIdx)
                .toList());

        // 값 리스트
        for (long teamIdx : teamIdxList) {
            if (checklistUserList.contains(teamIdx)) {
                continue;
            }

            ChecklistUserLinkDto checklistUserLinkDto = ChecklistUserLinkDto.builder()
                    .checklistIdx(checklistIdx)
                    .teamIdx(teamIdx)
                    .build();

            valueList.add(checklistUserLinkDto);
            checklistUserList.add(teamIdx);
        }

        if (valueList.isEmpty()) {
            return 0;
        }

        return insertChecklistUserLink(valueList);
    }

    /*
     * insert checklist user link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertChecklistUserLinkByUserIdx(long checklistIdx, List<Long> userIdxList) {
        List<ChecklistUserLinkDto> valueList = new ArrayList<>();
        List<Long> checklistUserList = new ArrayList<>(getChecklistUserLink(checklistIdx).stream()
                .map(ChecklistUserLinkWithUserDto::getUserIdx)
                .toList());

        // 값 리스트
        for (long userIdx : userIdxList) {
            if (checklistUserList.contains(userIdx)) {
                continue;
            }

            ChecklistUserLinkDto checklistUserLinkDto = ChecklistUserLinkDto.builder()
                    .checklistIdx(checklistIdx)
                    .userIdx(userIdx)
                    .build();

            valueList.add(checklistUserLinkDto);
            checklistUserList.add(userIdx);
        }

        if (valueList.isEmpty()) {
            return 0;
        }

        return insertChecklistUserLink(valueList);
    }
}
