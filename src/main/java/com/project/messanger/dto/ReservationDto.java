package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private long reservationIdx;
    private long roomIdx;
    private String description;
    private String startDate;
    private String endDate;
    private long approverIdx;
    private long creatorIdx;
    private String createdDate;
    private String updatedDate;
}
