package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingGoalRepository extends JpaRepository<SavingGoal, Integer> {
    Optional<SavingGoal> findByName(String name);
}
