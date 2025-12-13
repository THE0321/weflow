package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingMessageDto {
    private long messageIdx;
    private long chattingIdx;
    private long fileIdx;
    private String content;
    private long creatorIdx;
    private String createdDate;
}
