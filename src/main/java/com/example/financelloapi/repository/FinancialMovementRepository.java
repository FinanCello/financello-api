package com.example.financelloapi.repository;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialMovementRepository extends JpaRepository<FinancialMovement , Integer> {
    boolean existsByCategory_Id(Integer categoryId);
    List<FinancialMovement> findByUser_IdOrderByDateDesc(Integer userId);
    List<FinancialMovement> findByUser_IdAndMovementTypeOrderByDateDesc(Integer userId, MovementType type);
}