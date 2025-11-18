package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalUserLinkDto {
    private long linkIdx;
    private long goalIdx;
    private long teamIdx;
    private long userIdx;
}
