package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamUserLinkDto {
    private long linkIdx;
    private long userIdx;
    private long teamIdx;
}
