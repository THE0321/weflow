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

    List<ChecklistUserLinkDto> getChecklistUserLink(long checklistIdx);

    List<ChecklistLogDto> getChecklistLog(long checklistIdx);

    long insertChecklist(ChecklistDto checklistDto);

    int updateChecklist(ChecklistDto checklistDto);

    int deleteChecklist(long checklistIdx);

    int insertChecklistItem(List<ChecklistItemDto> list);

    int updateChecklistItem(ChecklistItemDto checklistItemDto);

    int insertChecklistLog(ChecklistLogDto checklistLogDto);

    int updateChecklistLog(ChecklistLogDto checklistLogDto);

    int deleteChecklistLog(long logIdx);
}
