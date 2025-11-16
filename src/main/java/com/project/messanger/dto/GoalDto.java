package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
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
}
