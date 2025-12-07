package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleAttenderLinkDto {
    private long linkIdx;
    private long scheduleIdx;
    private long userIdx;
    private String isAttend;
}
