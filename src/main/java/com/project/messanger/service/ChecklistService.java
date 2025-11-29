package com.project.messanger.service;

import com.project.messanger.dto.ChecklistDto;
import com.project.messanger.dto.ChecklistItemDto;
import com.project.messanger.dto.GoalDto;
import com.project.messanger.mapper.ChecklistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChecklistService {
    private final ChecklistMapper checklistMapper;

    public ChecklistService(ChecklistMapper checklistMapper) {
        this.checklistMapper = checklistMapper;
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
     * @param List<String>
     * return int
     */
    @Transactional
    public int updateChecklistItem(ChecklistItemDto checklistItemDto) {
        return checklistMapper.updateChecklistItem(checklistItemDto);
    }
}
