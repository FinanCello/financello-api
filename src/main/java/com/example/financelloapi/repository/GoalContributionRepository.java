package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalContributionRepository extends JpaRepository<GoalContribution, Integer> {
    List<GoalContribution> findGoalContributionsByDate(LocalDate date);
    Optional<GoalContribution> findById(Integer id);
}