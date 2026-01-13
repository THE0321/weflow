package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamUserLinkWithUserDto {
    private long linkIdx;
    private long userIdx;
    private String email;
    private String userName;
    private long teamIdx;
    private String teamName;
}
