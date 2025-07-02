package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUserId(Integer userId);  // Cambiar a Integer
    boolean existsByUserIdAndAchievementId(Integer userId, Long achievementId);  // Mantener Integer
}
