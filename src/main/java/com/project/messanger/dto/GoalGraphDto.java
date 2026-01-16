package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalGraphDto {
    private String logDate;
    private long progressValue;
    private long dailyIncrease;
}
