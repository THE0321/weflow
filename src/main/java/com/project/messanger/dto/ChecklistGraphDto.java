package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistGraphDto {
    private String logDate;
    private long checkCount;
    private long dailyIncrease;
}
