package com.project.messanger.util;

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
        // 세션에서 로그인 데이터 꺼내서 권한 체크
        UserDto loginInfo = (UserDto)session.getAttribute("login_info");
        if (loginInfo == null) {
            return false;
        }

        return loginInfo.getAdminYn().equals("Y") || loginInfo.getLeaderYn().equals("Y");
    }

    public boolean authCheck(HttpSession session, boolean onlyAdmin){
        if (!onlyAdmin) {
            return authCheck(session);
        }

        // 세션에서 로그인 데이터 꺼내서 권한 체크
        UserDto loginInfo = (UserDto)session.getAttribute("login_info");
        if (loginInfo == null) {
            return false;
        }

        return loginInfo.getAdminYn().equals("Y");
    }
}
