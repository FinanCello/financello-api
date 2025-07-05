package com.example.financelloapi.api;

import com.example.financelloapi.dto.response.AchievementDTO;
import com.example.financelloapi.dto.response.UserAchievementDTO;
import com.example.financelloapi.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/achievements")

public class AchievementController {
    @Autowired
    private AchievementService achievementService;

    @GetMapping
    public List<AchievementDTO> getAllAchievements() {
        return achievementService.getAllAchievements();
    }

    @GetMapping("/user/{userId}")
    public List<UserAchievementDTO> getUserAchievements(@PathVariable Integer userId) {
        return achievementService.getUserAchievements(userId);
    }

    @PostMapping("/user/{userId}/check")
    public void checkAchievements(@PathVariable Integer userId) {
        achievementService.checkAndUnlockAchievements(userId);
    }
}
