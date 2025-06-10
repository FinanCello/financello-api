package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.exception.DuplicateResourceException;
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
        // 1) Validación: si ya existe una meta con el mismo nombre, error 400
        if (savingGoalRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateResourceException("Ya existe una meta con ese nombre");
        }

        // 2) Si pasó, creamos la nueva meta
        SavingGoal goal = new SavingGoal();
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setDueDate(request.dueDate());
        // inicializamos currentAmount a cero, asignamos el usuario, etc.
        goal.setCurrentAmount(0.0f);
        SavingGoal saved = savingGoalRepository.save(goal);
        return savingGoalMapper.toResponse(saved);
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
      
    @Override
    public AddSavingGoalResponse updateSavingGoal(Integer goalId, UpdateSavingGoalRequest request) {
        // 1) Buscamos la meta por ID
        SavingGoal goal = savingGoalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("Meta no encontrada"));

        // 2) Validaciones de negocio
        if (request.targetAmount() == null || request.targetAmount() < 0.0f) {
            throw new IllegalArgumentException("El monto debe ser mayor o igual a 0");
        }
        if (request.dueDate() == null || request.dueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha no válida");
        }

        // 3) Aplicamos cambios y guardamos
        goal.setTargetAmount(request.targetAmount());
        goal.setDueDate(request.dueDate());
        SavingGoal updated = savingGoalRepository.save(goal);

        // 4) Devolvemos DTO de respuesta
        return savingGoalMapper.toResponse(updated);
    }
}
