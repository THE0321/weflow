package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDto {
    private long noticeIdx;
    private String title;
    private String content;
    private String linkUrl;
    private String startDate;
    private String endDate;
    private String isActive;
    private long creatorIdx;
    private String createdDate;
}
