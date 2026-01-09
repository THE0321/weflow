package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AILogDto {
    private long aiLogIdx;
    private String feature;
    private String prompt;
    private String result;
    private long creatorIdx;
    private String createdDate;
}
