package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistWithUserDto {
    private long checklistIdx;
    private String title;
    private String description;
    private String status;
    private long creatorIdx;
    private String creatorName;
    private String createdDate;
    private String updatedDate;
}
