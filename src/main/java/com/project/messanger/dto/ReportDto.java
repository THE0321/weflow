package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {
    private long reportIdx;
    private String title;
    private String description;
    private String type;
    private long approverIdx;
    private long creatorIdx;
    private String createdDate;
    private String updatedDate;
}
