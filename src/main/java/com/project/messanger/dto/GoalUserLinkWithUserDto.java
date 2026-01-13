package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalUserLinkWithUserDto {
    private long linkIdx;
    private long goalIdx;
    private long teamIdx;
    private String teamName;
    private long userIdx;
    private String email;
    private String userName;
}
