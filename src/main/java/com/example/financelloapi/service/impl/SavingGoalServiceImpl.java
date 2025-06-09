package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.exception.SavingGoalHasContributionsException;
import com.example.financelloapi.exception.TargetAmountLessThanCurrentAmountException;
import com.example.financelloapi.mapper.SavingGoalMapper;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class SavingGoalServiceImpl implements SavingGoalService{
    private final SavingGoalRepository savingGoalRepository;
    private final GoalContributionRepository goalContributionRepository;
    private final SavingGoalMapper savingGoalMapper;

    @Override
    public AddSavingGoalResponse addSavingGoal(AddSavingGoalRequest request) {
        SavingGoal goal = savingGoalRepository.findByName(request.name())
                .orElseThrow(() -> new NoSuchElementException("Meta no encontrada"));

        if (request.targetAmount() <= 0.0f) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        if (request.targetAmount() <= goal.getCurrentAmount()) {
            throw new TargetAmountLessThanCurrentAmountException();
        }

        if (request.dueDate() == null || request.dueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento debe ser hoy o futura");
        }

        goal.setTargetAmount(request.targetAmount());

        SavingGoal savedGoal = savingGoalRepository.save(goal);
        return savingGoalMapper.toResponse(savedGoal);
    }

    @Override
    public void deleteSavingGoal(Integer goalId) {
        SavingGoal goal = savingGoalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Meta no encontrada: " + goalId));

        // TA01: validar que no tenga aportes
        if (goal.getCurrentAmount() != null && goal.getCurrentAmount() > 0.0f) {
            throw new SavingGoalHasContributionsException(goalId);
        }

        // TA02: eliminación
        savingGoalRepository.delete(goal);
    }
}
