package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private long notificationIdx;
    private String type;
    private String content;
    private String isRead;
    private String linkUrl;
    private long creatorIdx;
    private String createdDate;
}
