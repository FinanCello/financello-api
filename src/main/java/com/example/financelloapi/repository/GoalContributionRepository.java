package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoalContributionRepository extends JpaRepository<GoalContribution, Integer> {
    Optional<GoalContribution> findById(Integer id);
}