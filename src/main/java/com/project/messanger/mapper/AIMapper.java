package com.project.messanger.mapper;

import com.project.messanger.dto.AILogDto;
import com.project.messanger.dto.AISchemaDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AIMapper {
    List<String> getFeatureList();

    List<AISchemaDto> getColumnList(String feature);

    void insertAILog(AILogDto aiLogDto);
}
