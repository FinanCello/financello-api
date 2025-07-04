package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.Achievement;
import com.example.financelloapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
