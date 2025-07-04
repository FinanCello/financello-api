package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.response.AchievementDTO;
import com.example.financelloapi.dto.response.UserAchievementDTO;
import com.example.financelloapi.mapper.AchievementMapper;
import com.example.financelloapi.model.entity.Achievement;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.entity.UserAchievement;
import com.example.financelloapi.model.enums.SavingGoalProgress;
import com.example.financelloapi.repository.*;
import com.example.financelloapi.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AchievementServiceImpl implements AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementMapper mapper;

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private GoalContributionRepository goalContributionRepository;

    @Override
    public List<AchievementDTO> getAllAchievements() {
        return achievementRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserAchievementDTO> getUserAchievements(Integer userId) {
        return userAchievementRepository.findByUserId(userId)
                .stream()
                .map(mapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void checkAndUnlockAchievements(Integer userId) { //aca es donde se decide  si el usuario gana un logro
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Achievement> allAchievements = achievementRepository.findAll();

        // Obtener valores reales desde la base de datos
        int completedGoals = savingGoalRepository.countByUserIdAndProgress(userId, SavingGoalProgress.DONE);
        int contributionCount = goalContributionRepository.countBySavingGoal_User_Id(userId);
        float totalSaved = Optional.ofNullable(goalContributionRepository.sumTotalByUserId(userId)).orElse(0.0f);

        // Calcular racha de días consecutivos
        List<GoalContribution> contributions = goalContributionRepository.findBySavingGoal_User_IdOrderByDateAsc(userId);

        int maxStreak = 0;
        int currentStreak = 0;
        LocalDate prevDate = null;

        for (GoalContribution c : contributions) {
            LocalDate date = c.getDate();

            if (prevDate == null || date.equals(prevDate.plusDays(1))) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else if (!date.equals(prevDate)) {
                currentStreak = 1;
            }

            prevDate = date;
        }

        // Verificar cada logro
        for (Achievement achievement : allAchievements) {

            boolean alreadyUnlocked = userAchievementRepository
                    .existsByUserIdAndAchievementId(userId, achievement.getId());

            if (alreadyUnlocked) continue;

            String type = achievement.getTriggerType();
            int requiredValue = achievement.getTriggerValue();

            boolean meetsCondition = switch (type) {
                case "GOAL_COMPLETED" -> completedGoals >= requiredValue;
                case "CONTRIBUTION_COUNT" -> contributionCount >= requiredValue;
                case "TOTAL_SAVED" -> totalSaved >= requiredValue;
                case "STREAK_DAYS" -> maxStreak >= requiredValue;
                default -> false;
            };

            if (meetsCondition) { //si cumple la condición se desbloquea y se guarda
                UserAchievement ua = new UserAchievement();
                ua.setUser(user);
                ua.setAchievement(achievement);
                ua.setUnlockedAt(LocalDateTime.now());
                userAchievementRepository.save(ua);
            }
        }
    }
}
