package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetingAttenderLinkWithUserDto {
    private long linkIdx;
    private long reservationIdx;
    private long userIdx;
    private String email;
    private String userName;
    private String isAttend;
}
