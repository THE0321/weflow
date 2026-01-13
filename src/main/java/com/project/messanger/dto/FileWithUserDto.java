package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileWithUserDto {
    private long fileIdx;
    private String name;
    private String filePath;
    private long size;
    private String isDrive;
    private long creatorIdx;
    private String creatorName;
    private String createdDate;
}
