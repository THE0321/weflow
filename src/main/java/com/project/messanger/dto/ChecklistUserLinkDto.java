package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistUserLinkDto {
    private long linkIdx;
    private long checklistIdx;
    private long teamIdx;
    private long userIdx;
}
