package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingDto {
    private long chattingIdx;
    private String name;
    private long creatorIdx;
    private String createdDate;
}
