package com.project.messanger.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.messanger.mapper.AIMapper;
import com.project.messanger.service.AIService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@NoArgsConstructor
public class GeminiUtil {
    private final WebClient webClient = WebClient.create("https://generativelanguage.googleapis.com");

    @Value("${gemini.api.version}")
    private String apiVersion;

    @Value("${gemini.api.key}")
    private String apiKey;

    // 답변내용이랑 필요한 작업이 파라미터로 들어감
    public Map<String, Object> callGemini(String prompt, List<String> featureList) throws JsonProcessingException {
        // 파라메터 세팅
        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of(  // AI에 명령 및 히스토리(이력관리) 보냄
                "parts", List.of(Map.of("text", prompt))
            )),
            "generationConfig", Map.of(  // AI가 해주는 답변
                "responseMimeType", "application/json",
                "responseJsonSchema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "content", Map.of(
                            "type", "string",
                            "description", "응답 메세지"
                        ),
                        "command", Map.of(
                            "type", "string",
                            "description", "지금 사용자가 원하는게 어떤 종류의 명령이야?",
                            "enum", featureList
                        )
                    ),
                    "required", List.of("content")
                )
            )
        );

        // GEMINI AI 호출
        Mono<String> output = webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/v1beta/models/" + apiVersion + ":generateContent")
                .queryParam("key", apiKey)
                .build())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultData = objectMapper.readValue(output.block(), HashMap.class);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);

        List<LinkedHashMap<String, Object>> candidates = (List<LinkedHashMap<String, Object>>) resultData.get("candidates");
        if (candidates.isEmpty()) {
            return result;
        }

        LinkedHashMap<String, Object> candidate = candidates.get(0);
        if (candidate.get("content") == null || !candidate.get("finishReason").equals("STOP")) {
            return result;
        }

        LinkedHashMap<String, Object> content = (LinkedHashMap<String, Object>) candidate.get("content");
        if (content.get("parts") == null) {
            return result;
        }

        List<LinkedHashMap<String, Object>> parts = (List<LinkedHashMap<String, Object>>) content.get("parts");
        if (parts.isEmpty()) {
            return result;
        }

        LinkedHashMap<String, Object> part = parts.get(0);
        if (part.get("text") == null) {
            return result;
        }

        String value = (String) part.get("text");

        return result;
    }

    // 컬럼리스트에다가 값을 세팅해서 보내주는 게 필요함
    public Map<String, Object> getData(Map<String, Object> resultData) {

    }
}
