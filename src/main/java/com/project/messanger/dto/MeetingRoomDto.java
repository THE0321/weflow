package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRoomDto {
    private long roomIdx;
    private String name;
    private String location;
    private int capacity;
}
