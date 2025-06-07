package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.exception.GoalContributionNotFoundException;
import com.example.financelloapi.exception.TargetAmountLessThanCurrentAmountException;
import com.example.financelloapi.mapper.SavingGoalMapper;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SavingGoalServiceImpl implements SavingGoalService{
    private final SavingGoalRepository savingGoalRepository;
    private final GoalContributionRepository goalContributionRepository;
    private final SavingGoalMapper savingGoalMapper;

    @Override
    public AddSavingGoalResponse addSavingGoal(AddSavingGoalRequest request) {
        Optional<SavingGoal> goalOpt = savingGoalRepository.findByName(request.name());
        if (goalOpt.isEmpty()) {
            throw new NoSuchElementException("Meta no encontrada");
        }
        SavingGoal goal = goalOpt.get();

        if (request.targetAmount() <= goal.getCurrentAmount()) {
            throw new TargetAmountLessThanCurrentAmountException();
        }

        if (request.targetAmount() <= 0.0f) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        if (request.dueDate() == null || request.dueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento debe ser hoy o futura");
        }

        // Aquí puedes actualizar la meta con los nuevos datos (por ejemplo targetAmount, dueDate, etc)
        goal.setTargetAmount(request.targetAmount());
        // Asumiendo que quieres sumar o modificar currentAmount si corresponde
        // goal.setCurrentAmount(...);

        SavingGoal savedGoal = savingGoalRepository.save(goal);

        return savingGoalMapper.toResponse(savedGoal);
    }
}
