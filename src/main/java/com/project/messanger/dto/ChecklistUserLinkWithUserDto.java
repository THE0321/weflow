package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistUserLinkWithUserDto {
    private long linkIdx;
    private long checklistIdx;
    private long teamIdx;
    private String teamName;
    private long userIdx;
    private String email;
    private String userName;
}
