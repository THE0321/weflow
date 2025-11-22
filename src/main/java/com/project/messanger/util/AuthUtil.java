package com.project.messanger.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.messanger.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class AuthUtil {
    public boolean authCheck(HttpSession session){
        try {
            // 세션에서 로그인 데이터 꺼내서 권한 체크
            UserDto loginInfo = getLoginInfo(session);
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
            UserDto loginInfo = getLoginInfo(session);
            if (loginInfo == null) {
                return false;
            }

            return loginInfo.getAdminYn().equals("Y");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean loginCheck(HttpSession session){
        return getLoginInfo(session) != null;
    }

    public UserDto getLoginInfo(HttpSession session){
        try {
            String jsonString = (String)session.getAttribute("login_info");

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, UserDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Long> getTeamList(HttpSession session) {
        try {
            String jsonString = (String)session.getAttribute("team_idx_list");

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, ArrayList.class);
        } catch (Exception e) {
            return null;
        }
    }
}
