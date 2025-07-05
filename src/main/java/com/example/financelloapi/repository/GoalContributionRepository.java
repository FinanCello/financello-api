package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalContributionRepository extends JpaRepository<GoalContribution, Integer> {
    List<GoalContribution> findGoalContributionsByDate(LocalDate date);
    Optional<GoalContribution> findById(Integer id);

    int countBySavingGoal_User_Id(Integer id); //N° total de contribuciones usuario

    @Query("SELECT COALESCE(SUM(gc.amount), 0) FROM GoalContribution gc WHERE gc.savingGoal.user.id = :userId")
    float sumTotalByUserId(@Param("userId") Integer userId); // Suma total de dinero ahorrado por el usuario

    List<GoalContribution> findBySavingGoal_User_IdOrderByDateAsc(Integer userId); // Lista ordenada de contribuciones por fechas (para racha diaria)
    
    List<GoalContribution> findBySavingGoal_User_Id(Integer userId); // Obtener todas las contribuciones de un usuario
    
    List<GoalContribution> findBySavingGoal_User_IdAndSavingGoal_Id(Integer userId, Integer goalId); // Obtener contribuciones de un usuario a una meta específica
}