package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GoalContributionRepository extends JpaRepository<GoalContribution, Integer> {
    List<GoalContribution> findGoalContributionsByDate(LocalDate date);
}
