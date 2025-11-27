package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalAndLogDto {
    private long goalIdx;
    private String title;
    private String description;
    private String status;
    private long targetValue;
    private long currentValue;
    private String startDate;
    private String endDate;
    private long creatorIdx;
    private String createdDate;
    private String updatedDate;

    private long progressValue;
    private String content;
    private String logDate;
}
