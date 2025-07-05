package com.example.financelloapi.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserAchievementDTO {
    private String achievementName;
    private String achievementDescription;
    private LocalDateTime unlockedAt;
}
