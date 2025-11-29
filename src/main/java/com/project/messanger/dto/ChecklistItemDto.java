package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistItemDto {
    private long itemIdx;
    private long checklistIdx;
    private String title;
    private String description;
}
