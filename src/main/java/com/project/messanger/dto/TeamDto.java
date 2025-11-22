package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    private long teamIdx;
    private String teamName;
    private String createdDate;
    private String updatedDate;
}
