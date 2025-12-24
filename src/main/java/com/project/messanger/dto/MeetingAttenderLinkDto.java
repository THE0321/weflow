package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetingAttenderLinkDto {
    private long linkIdx;
    private long reservationIdx;
    private long userIdx;
    private String isAttend;
}
