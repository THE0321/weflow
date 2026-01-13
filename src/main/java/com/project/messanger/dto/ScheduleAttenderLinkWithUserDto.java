package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleAttenderLinkWithUserDto {
    private long linkIdx;
    private long scheduleIdx;
    private long userIdx;
    private String email;
    private String userName;
    private String isAttend;
}
