package com.project.messanger.controller;

import com.project.messanger.dto.UserDto;
import com.project.messanger.service.UserService;
import com.project.messanger.util.AuthUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthUtil authUtil;

    @Test
    @DisplayName("회원 리스트 조회 테스트")
    void getUserListTest() throws Exception {
        // given
        UserDto userDto = UserDto.builder()
                .userIdx(1L)
                .email("test@example.com")
                .userName("테스터")
                .build();
        List<UserDto> userList = List.of(userDto);

        // 권한 체크 통과 설정
        given(authUtil.authCheck(any())).willReturn(true);
        // 서비스 호출 결과 설정
        given(userService.getUserList(anyMap())).willReturn(userList);

        // when
        ResultActions resultActions = mockMvc.perform(post("/user/list")
                .param("page", "1")
                .param("limit", "20"));

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.list[0].email").value("test@example.com"));
    }
}
