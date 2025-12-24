package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistLogDto {
    private long logIdx;
    private long itemIdx;
    private String isChecked;
    private String content;
    private long creatorIdx;
    private String createdDate;
}
