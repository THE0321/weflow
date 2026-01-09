package com.project.messanger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.messanger.dto.AILogDto;
import com.project.messanger.dto.AISchemaDto;
import com.project.messanger.mapper.AIMapper;
import com.project.messanger.util.GeminiUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private final AIMapper aiMapper;
    private final GeminiUtil geminiUtil;

    public AIService(AIMapper aiMapper, GeminiUtil geminiUtil) {
        this.aiMapper = aiMapper;
        this.geminiUtil = geminiUtil;
    }

    /*
     * get feature list
     * return List<String>
     */
    @Transactional(readOnly = true)
    public List<String> getFeatureList() {
        return aiMapper.getFeatureList();
    }

    @Transactional(readOnly = true)
    public List<AISchemaDto> getColumnList(String feature) {
        return aiMapper.getColumnList(feature);
    }

    /*
     * insert AI log
     * param AILogDto
     * return int
     */
    @Transactional
    public void insertAILog(AILogDto aiLogDto) {
        aiMapper.insertAILog(aiLogDto);
    }

    /*
     * call gemini api
     * param String
     * return Map<String, Object>
     */
    @Transactional(readOnly = true)
    public Map<String, Object> callGemini(String prompt) throws JsonProcessingException {
        Map<String, Object> result = geminiUtil.callGemini(prompt, getFeatureList());
        if (result.get("command") != null) {
            List<AISchemaDto> schemaDtoList = getColumnList(result.get("command").toString());
            result.put("properties", schemaDtoList);

            Map<String, Object> properties = geminiUtil.getData(result);
            for (AISchemaDto schemaDto : schemaDtoList) {
                if (!properties.containsKey(schemaDto.getName())) {
                    properties.put(schemaDto.getName(), null);
                }
            }
            result.put("properties", properties);
        }

        return result;
    }
}
