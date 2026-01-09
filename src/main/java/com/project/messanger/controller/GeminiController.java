package com.project.messanger.controller;

import com.project.messanger.util.GeminiUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class GeminiController {
    private final GeminiUtil geminiUtil;

    public GeminiController(GeminiUtil geminiUtil) {
        this.geminiUtil = geminiUtil;
    }

    @PostMapping("/test")
    public Map<String, Object> test(){
        Map<String, Object> result = new HashMap<>();

        try {
            geminiUtil.test("김 아무개 등록해줘");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
