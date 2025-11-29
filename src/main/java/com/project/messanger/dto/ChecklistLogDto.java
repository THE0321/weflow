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
    private long userIdx;
    private String content;
    private String createdDate;
}
