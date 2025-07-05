package com.example.financelloapi.service;

import com.example.financelloapi.dto.response.AchievementDTO;
import com.example.financelloapi.dto.response.UserAchievementDTO;

import java.util.List;

public interface AchievementService {
    List<AchievementDTO> getAllAchievements();
    List<UserAchievementDTO> getUserAchievements(Integer userId); // Cambiar a Integer
    void checkAndUnlockAchievements(Integer userId); // Mantener en Integer
}

