package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {
    private long scheduleIdx;
    private String title;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private String isAllday;
    private long approverIdx;
    private long creatorIdx;
    private String createdDate;
    private String updatedDate;
}
