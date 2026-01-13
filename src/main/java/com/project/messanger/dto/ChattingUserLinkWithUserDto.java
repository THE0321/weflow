package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingUserLinkWithUserDto {
    private long linkIdx;
    private long chattingIdx;
    private long userIdx;
    private String email;
    private String userName;
}
