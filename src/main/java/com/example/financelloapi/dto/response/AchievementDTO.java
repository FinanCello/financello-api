package com.example.financelloapi.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String triggerType;
    private Integer triggerValue;
}