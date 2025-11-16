package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long userIdx;
    private String email;
    private String password;
    private String userName;
    private String phoneNumber;
    private String adminYn;
    private String leaderYn;
    private String isDeleted;
    private String createdDate;
    private String updatedDate;
}
