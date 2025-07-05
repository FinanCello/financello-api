package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.dto.response.UserGoalsWithContributionsResponse;
import com.example.financelloapi.exception.CurrentAmountExceedsTargetException;
import com.example.financelloapi.exception.SavingGoalHasContributionsException;
import com.example.financelloapi.exception.TargetAmountLessThanCurrentAmountException;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.exception.UserGoalsNotFoundException;
import com.example.financelloapi.mapper.SavingGoalMapper;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.SavingGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SavingGoalServiceImpl implements SavingGoalService{
    private final SavingGoalRepository savingGoalRepository;
    private final SavingGoalMapper savingGoalMapper;
    private final UserRepository userRepository;

    @Override
    public AddSavingGoalResponse addSavingGoal(Integer userId, AddSavingGoalRequest request) {
        // 1) validaciones del DTO…
        if (request.targetAmount() == null || request.targetAmount() <= 0.0f) {
            throw new IllegalArgumentException("El monto objetivo debe ser mayor a 0");
        }
        
        // 2) obtenemos el User o lanzamos excepción
        User user = userRepository.findByIdCustom(userId)
                .orElseThrow(() -> new UserDoesntExistException("Usuario no encontrado: " + userId));

        // 3) comprobamos si ya existe una meta con ese nombre…
        Optional<SavingGoal> existingOpt = savingGoalRepository
                .findByName(request.name());
        if (existingOpt.isPresent()) {
            // lógica de actualización parcial…
            SavingGoal updated = existingOpt.get();
            // …
            return savingGoalMapper.toResponse(updated);
        }

        // 4) si no existe, creamos una nueva entidad y le asignamos el user
        SavingGoal goal = savingGoalMapper.toEntity(request, user);
        goal.setCurrentAmount(0.0f);
        
        // 5) Validar que current_amount no exceda target_amount (redundante pero por seguridad)
        if (goal.getCurrentAmount() > goal.getTargetAmount()) {
            throw new CurrentAmountExceedsTargetException(
                "El monto actual ($" + goal.getCurrentAmount() + 
                ") no puede ser mayor al monto objetivo ($" + goal.getTargetAmount() + ")"
            );
        }
        
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
        if (request.targetAmount() == null || request.targetAmount() <= 0.0f) {
            throw new IllegalArgumentException("El monto objetivo debe ser mayor a 0");
        }
        if (request.dueDate() == null || request.dueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento debe ser hoy o futura");
        }
        
        // Validar que el nuevo monto objetivo no sea menor al monto actual
        if (goal.getCurrentAmount() != null && request.targetAmount() < goal.getCurrentAmount()) {
            throw new TargetAmountLessThanCurrentAmountException(
                "No se puede reducir la meta objetivo a $" + request.targetAmount() + 
                " porque ya se han acumulado $" + goal.getCurrentAmount() + 
                ". La meta objetivo debe ser mayor o igual al monto actual acumulado."
            );
        }

        // 3) Aplicamos cambios y guardamos
        goal.setTargetAmount(request.targetAmount());
        goal.setDueDate(request.dueDate());
        SavingGoal updated = savingGoalRepository.save(goal);

        // 4) Devolvemos DTO de respuesta
        return savingGoalMapper.toResponse(updated);
    }

    @Override
    public List<AddSavingGoalResponse> getGoalsByUser(Integer userId) {
        // valida existencia de usuario
        userRepository.findByIdCustom(userId)
                .orElseThrow(() -> new UserDoesntExistException("Usuario no encontrado: " + userId));

        // recoge todas las metas de ese usuario
        List<SavingGoal> goals = savingGoalRepository.findByUserId(userId);
        return goals.stream()
                .map(savingGoalMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserGoalsWithContributionsResponse> getUserGoalsWithContributions(Integer userId) {
        // valida existencia de usuario
        userRepository.findByIdCustom(userId)
                .orElseThrow(() -> new UserDoesntExistException("Usuario no encontrado: " + userId));

        // recoge todas las metas de ese usuario
        List<SavingGoal> goals = savingGoalRepository.findByUserId(userId);
        if (goals.isEmpty()) {
            throw new UserGoalsNotFoundException(userId);
        }
        
        return goals.stream()
                .map(UserGoalsWithContributionsResponse::fromSavingGoal)
                .toList();
    }
}


