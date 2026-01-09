package com.project.messanger.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AISchemaDto {
    private long schemaIdx;
    private String feature;
    private String isUse;
    private String isRequired;
    private String columnKey;
    private String name;
    private String description;
}
