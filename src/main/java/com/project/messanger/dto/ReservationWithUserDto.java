package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationWithUserDto {
    private long reservationIdx;
    private long roomIdx;
    private String description;
    private String startDate;
    private String endDate;
    private long approverIdx;
    private String approverName;
    private long creatorIdx;
    private String creatorName;
    private String createdDate;
    private String updatedDate;
}
