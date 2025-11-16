package com.project.messanger.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.messanger.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@NoArgsConstructor
public class AuthUtil {
    public boolean authCheck(HttpSession session){
        try {
            // 세션에서 로그인 데이터 꺼내서 권한 체크
            String jsonString = (String)session.getAttribute("login_info");

            ObjectMapper objectMapper = new ObjectMapper();
            UserDto loginInfo = objectMapper.readValue(jsonString, UserDto.class);
            if (loginInfo == null) {
                return false;
            }

            return loginInfo.getAdminYn().equals("Y") || loginInfo.getLeaderYn().equals("Y");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean authCheck(HttpSession session, boolean onlyAdmin){
        try {
            if (!onlyAdmin) {
                return authCheck(session);
            }

            // 세션에서 로그인 데이터 꺼내서 권한 체크
            String jsonString = (String)session.getAttribute("login_info");

            ObjectMapper objectMapper = new ObjectMapper();
            UserDto loginInfo = objectMapper.readValue(jsonString, UserDto.class);
            if (loginInfo == null) {
                return false;
            }

            return loginInfo.getAdminYn().equals("Y");
        } catch (Exception e) {
            return false;
        }
    }
}
