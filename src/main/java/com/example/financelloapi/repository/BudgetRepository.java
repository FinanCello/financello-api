package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    Optional<Budget> findByUser_IdAndPeriod(Integer userId, String period);
    List<Budget> findByUser_Id(Integer userId);
}
