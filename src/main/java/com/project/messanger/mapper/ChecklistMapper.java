package com.project.messanger.mapper;

import com.project.messanger.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChecklistMapper {
    long getChecklistCount(Map<String, Object> param);

    List<ChecklistWithUserDto> getChecklistList(Map<String, Object> param);

    List<ChecklistWithUserDto> getChecklistMainList(Map<String, Object> param);

    ChecklistDto getChecklistByIdx(long checklistIdx);

    List<ChecklistLogDto> getChecklistLog(long checklistIdx);

    int insertChecklist(ChecklistDto checklistDto);

    int updateChecklist(ChecklistDto checklistDto);

    int deleteChecklist(long checklistIdx);

    List<ChecklistItemDto> getChecklistItemList(long checklistIdx);

    ChecklistItemDto getChecklistItemByIdx(long itemIdx);

    int insertChecklistItem(List<ChecklistItemDto> list);

    int updateChecklistItem(ChecklistItemDto checklistItemDto);

    int deleteChecklistItem(List<Long> itemIdx);

    ChecklistLogWithUserDto getChecklistLogLast(long itemIdx);

    List<ChecklistUserLinkWithUserDto> getChecklistUserLink(long checklistIdx);
    
    int insertChecklistUserLink(List<ChecklistUserLinkDto> list);

    int deleteChecklistUserLinkByUserIdx(Map<String, Object> param);

    int deleteChecklistUserLinkByTeamIdx(Map<String, Object> param);

    int insertChecklistLog(ChecklistLogDto checklistLogDto);

    int updateChecklistLog(ChecklistLogDto checklistLogDto);

    int deleteChecklistLog(long logIdx);
}
