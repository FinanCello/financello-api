package com.example.financelloapi.repository;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FinancialMovementRepository extends JpaRepository<FinancialMovement , Integer> {
    boolean existsByCategory_Id(Integer categoryId);
    List<FinancialMovement> findByUser_IdOrderByDateDesc(Integer userId);
    List<FinancialMovement> findByUser_IdAndMovementTypeOrderByDateDesc(Integer userId, MovementType type);

    List<FinancialMovement> findByUser_IdAndCategory_IdAndMovementTypeAndDateBetween(Integer userId, Integer id, MovementType movementType, LocalDate start, LocalDate end);
    List<FinancialMovement> findByUser_Id(Integer userId);
    List<FinancialMovement> findByUser_IdAndCategory_Id(Integer userId, Integer categoryId);
    List<FinancialMovement> findByUser_IdAndMovementType(Integer userId, MovementType type);
    List<FinancialMovement> findByUser_IdAndCategory_IdAndMovementType(Integer userId, Integer categoryId, MovementType type);
}