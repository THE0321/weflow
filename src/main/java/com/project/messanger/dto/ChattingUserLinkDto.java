package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingUserLinkDto {
    private long linkIdx;
    private long chattingIdx;
    private long userIdx;
}
