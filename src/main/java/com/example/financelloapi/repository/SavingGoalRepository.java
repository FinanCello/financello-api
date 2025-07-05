package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.model.enums.SavingGoalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavingGoalRepository extends JpaRepository<SavingGoal, Integer> {
    Optional<SavingGoal> findByName(String name);
    List<SavingGoal> findByUserId(Integer userId);

    int countByUserIdAndProgress(Integer userId, SavingGoalProgress progress); //cantidad de metas cumplidas por el usuario
}
