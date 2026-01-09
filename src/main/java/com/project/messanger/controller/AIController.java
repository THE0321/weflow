package com.project.messanger.controller;

import com.project.messanger.dto.AILogDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.AIService;
import com.project.messanger.util.AuthUtil;
import com.project.messanger.util.GeminiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {
    private final AIService aiService;
    private final AuthUtil authUtil;

    public AIController(AIService aiService, AuthUtil authUtil) {
        this.aiService = aiService;
        this.authUtil = authUtil;
    }

    @PostMapping("/call")
    public Map<String, Object> callGemini(HttpServletRequest request,
                                          @RequestParam(value = "prompt") String prompt){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 제미나이 호출
            result = aiService.callGemini(prompt);
            result.put("success", true);

            // 파라메터 세팅
            AILogDto aiLogDto = AILogDto.builder()
                    .feature(result.get("command").toString())
                    .prompt(prompt)
                    .result(result.get("content").toString())
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            // 이력 저장
            aiService.insertAILog(aiLogDto);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "AI 결과를 가져오는데 실패했습니다.");
        }

        return result;
    }
}
