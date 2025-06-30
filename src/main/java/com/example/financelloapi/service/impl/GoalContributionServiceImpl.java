package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.exception.ContributionExceedsTargetException;
import com.example.financelloapi.exception.EmptyAmountException;
import com.example.financelloapi.exception.IncorrectDateException;
import com.example.financelloapi.mapper.GoalContributionMapper;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.model.enums.SavingGoalProgress;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.service.GoalContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GoalContributionServiceImpl implements GoalContributionService {

    private final GoalContributionRepository goalContributionRepository;
    private final GoalContributionMapper goalContributionMapper;
    private final SavingGoalRepository savingGoalRepository;

    @Override
    @Transactional
    public RegisterGoalContributionResponse registerGoalContribution(RegisterGoalContributionRequest request) {
        if (request.amount() <= 0) {
            throw new EmptyAmountException("Amount must be greater than zero");
        }

        // Buscar la meta de ahorro
        SavingGoal savingGoal = savingGoalRepository.findById(request.goalId())
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        // Calcular el nuevo current_amount después de la contribución
        Float newCurrentAmount = savingGoal.getCurrentAmount() + request.amount();

        // Verificar que la contribución no exceda la meta objetivo
        if (newCurrentAmount > savingGoal.getTargetAmount()) {
            throw new ContributionExceedsTargetException(
                "Esta contribución de $" + request.amount() + 
                " haría que se sobrepase la meta objetivo. " +
                "Meta objetivo: $" + savingGoal.getTargetAmount() + 
                ", Monto actual: $" + savingGoal.getCurrentAmount() + 
                ". Por favor, ajuste el monto de la contribución o actualice la meta objetivo."
            );
        }

        // Crear y guardar la contribución
        GoalContribution goalContribution = new GoalContribution();
        goalContribution.setDate(request.date());
        goalContribution.setAmount(request.amount());
        goalContribution.setSavingGoal(savingGoal);

        goalContributionRepository.save(goalContribution);

        // Actualizar el current_amount de la meta de ahorro
        savingGoal.setCurrentAmount(newCurrentAmount);

        // Verificar si la meta se ha completado
        if (newCurrentAmount.equals(savingGoal.getTargetAmount())) {
            savingGoal.setProgress(SavingGoalProgress.DONE);
        }

        // Guardar los cambios en la meta de ahorro
        savingGoalRepository.save(savingGoal);

        return goalContributionMapper.toResponse(goalContribution);
    }

    @Override
    public List<GoalContribution> historyGoalContributionsByDate(LocalDate date) {
        List<GoalContribution> contributions = goalContributionRepository.findGoalContributionsByDate(date);
        if (contributions.isEmpty()) {
            throw new IncorrectDateException();
        }
        return contributions;
    }

    @Override
    public List<GoalContribution> historyGoalContributions() {
        return goalContributionRepository.findAll();
    }
}