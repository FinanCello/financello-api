package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.FinancialMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialMovementRepository extends JpaRepository<FinancialMovement, Integer> {
}
