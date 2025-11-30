package com.project.messanger.service;

import com.project.messanger.dto.*;
import com.project.messanger.mapper.ChecklistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChecklistService {
    private final ChecklistMapper checklistMapper;

    public ChecklistService(ChecklistMapper checklistMapper) {
        this.checklistMapper = checklistMapper;
    }

    /*
     * get checklist list
     * @param Map<String, Object>
     * return List<ChecklistDto>
     */
    public List<ChecklistDto> getChecklistList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return checklistMapper.getChecklistList(param);
    }

    /*
     * get checklist main list
     * @param Map<String, Object>
     * return List<ChecklistDto>
     */
    public List<ChecklistDto> getChecklistMainList(Map<String, Object> param) {
        return checklistMapper.getChecklistMainList(param);
    }

    /*
     * get goal by goal idx
     * @param long
     * return goalDto
     */
    @Transactional(readOnly = true)
    public ChecklistDto getChecklistByIdx(long checklistIdx) {
        return checklistMapper.getChecklistByIdx(checklistIdx);
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
     * get checklist user link
     * @param long
     * return List<ChecklistUserLinkDto>
     */
    @Transactional(readOnly = true)
    public List<ChecklistUserLinkDto> getChecklistUserLink(long checklistIdx) {
        return checklistMapper.getChecklistUserLink(checklistIdx);
    }

    /*
     * insert checklist
     * @param ChecklistDto
     * return long
     */
    @Transactional
    public long insertChecklist(ChecklistDto checklistDto) {
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
     * insert checklist log
     * @param ChecklistLogDto
     * return int
     */
    @Transactional
    public int insertChecklistLog(ChecklistLogDto checklistLogDto) {
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
}
