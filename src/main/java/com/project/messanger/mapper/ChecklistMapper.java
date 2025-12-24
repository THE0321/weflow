package com.project.messanger.mapper;

import com.project.messanger.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChecklistMapper {
    List<ChecklistDto> getChecklistList(Map<String, Object> param);

    List<ChecklistDto> getChecklistMainList(Map<String, Object> param);

    ChecklistDto getChecklistByIdx(long checklistIdx);

    List<ChecklistLogDto> getChecklistLog(long checklistIdx);

    long insertChecklist(ChecklistDto checklistDto);

    int updateChecklist(ChecklistDto checklistDto);

    int deleteChecklist(long checklistIdx);

    ChecklistItemDto getChecklistItemByIdx(long itemIdx);

    int insertChecklistItem(List<ChecklistItemDto> list);

    int updateChecklistItem(ChecklistItemDto checklistItemDto);

    int deleteChecklistItem(List<Long> itemIdx);

    ChecklistLogDto getChecklistLogLast(long itemIdx);

    List<ChecklistUserLinkDto> getChecklistUserLink(long checklistIdx);
    
    int insertChecklistUserLink(List<ChecklistUserLinkDto> list);

    int deleteChecklistUserLink(List<Long> linkIdx);

    int insertChecklistLog(ChecklistLogDto checklistLogDto);

    int updateChecklistLog(ChecklistLogDto checklistLogDto);

    int deleteChecklistLog(long logIdx);
}
