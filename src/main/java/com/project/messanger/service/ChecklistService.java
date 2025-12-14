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
     * get checklist user link
     * @param long
     * return List<ChecklistUserLinkDto>
     */
    @Transactional(readOnly = true)
    public List<ChecklistUserLinkDto> getChecklistUserLink(long checklistIdx) {
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
     * delete checklist user link
     * @param List<Long>
     * return int
     */
    @Transactional
    public int deleteChecklistUserLink(List<Long> deleteLinkIdxList) {
        return checklistMapper.deleteChecklistUserLink(deleteLinkIdxList);
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

    /*
     * insert checklist user link by team idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertChecklistUserLinkByTeamIdx(long checklistIdx, List<Long> teamIdxList) {
        List<ChecklistUserLinkDto> insertList = new ArrayList<>();

        // 값 리스트
        for (long teamIdx : teamIdxList) {
            ChecklistUserLinkDto checklistUserLinkDto = ChecklistUserLinkDto.builder()
                    .checklistIdx(checklistIdx)
                    .teamIdx(teamIdx)
                    .build();
            insertList.add(checklistUserLinkDto);
        }

        return insertChecklistUserLink(insertList);
    }

    /*
     * insert checklist user link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertChecklistUserLinkByUserIdx(long checklistIdx, List<Long> userIdxList) {
        List<ChecklistUserLinkDto> insertList = new ArrayList<>();

        // 값 리스트
        for (long userIdx : userIdxList) {
            ChecklistUserLinkDto checklistUserLinkDto = ChecklistUserLinkDto.builder()
                    .checklistIdx(checklistIdx)
                    .userIdx(userIdx)
                    .build();
            insertList.add(checklistUserLinkDto);
        }

        return insertChecklistUserLink(insertList);
    }
}
