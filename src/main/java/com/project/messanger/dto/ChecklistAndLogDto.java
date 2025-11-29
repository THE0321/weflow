package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistAndLogDto {
    private long checklistIdx;
    private String title;
    private String description;
    private String status;
    private long creatorIdx;
    private String createdDate;
    private String updatedDate;

    private String content;
    private char isChecked;
    private String logDate;
}
