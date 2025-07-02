package com.example.financelloapi.mapper;

import com.example.financelloapi.dto.response.AchievementDTO;
import com.example.financelloapi.dto.response.UserAchievementDTO;
import com.example.financelloapi.model.entity.Achievement;
import com.example.financelloapi.model.entity.UserAchievement;
import org.springframework.stereotype.Component;

@Component

public class AchievementMapper {
    public AchievementDTO toDTO(Achievement achievement) {
        AchievementDTO dto = new AchievementDTO();
        dto.setId(achievement.getId());
        dto.setName(achievement.getName());
        dto.setDescription(achievement.getDescription());
        dto.setIcon(achievement.getIcon());
        dto.setTriggerType(achievement.getTriggerType());
        dto.setTriggerValue(achievement.getTriggerValue());
        return dto;
    }

    public UserAchievementDTO toUserDTO(UserAchievement ua){
        UserAchievementDTO dto = new UserAchievementDTO();
        dto.setAchievementName(ua.getAchievement().getName());
        dto.setAchievementDescription(ua.getAchievement().getDescription());
        dto.setUnlockedAt(ua.getUnlockedAt());
        return dto;
    }
}
