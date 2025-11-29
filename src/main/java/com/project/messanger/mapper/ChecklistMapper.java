package com.project.messanger.mapper;

import com.project.messanger.dto.ChecklistDto;
import com.project.messanger.dto.ChecklistItemDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChecklistMapper {
    long insertChecklist(ChecklistDto checklistDto);

    int updateChecklist(ChecklistDto checklistDto);

    int deleteChecklist(long checklistIdx);

    int insertChecklistItem(List<ChecklistItemDto> list);

    int updateChecklistItem(ChecklistItemDto checklistItemDto);
}
