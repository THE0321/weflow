package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportWithUserDto {
    private long reportIdx;
    private String title;
    private String description;
    private String type;
    private long approverIdx;
    private String approverName;
    private long creatorIdx;
    private String creatorName;
    private String createdDate;
    private String updatedDate;
}
