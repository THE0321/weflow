package com.project.messanger.mapper;

import com.project.messanger.dto.AILogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AIMapper {
    List<String> getFeatureList();

    void insertAILog(AILogDto aiLogDto);
}
