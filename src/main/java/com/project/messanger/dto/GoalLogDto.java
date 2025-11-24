package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalLogDto {
    private long logIdx;
    private long goalIdx;
    private long progressValue;
    private String content;
    private long creatorIdx;
    private String createdDate;
}
