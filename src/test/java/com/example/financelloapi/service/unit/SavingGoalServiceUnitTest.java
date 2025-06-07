package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.exception.TargetAmountLessThanCurrentAmountException;
import com.example.financelloapi.mapper.SavingGoalMapper;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.service.impl.SavingGoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SavingGoalServiceUnitTest {

    @Mock
    private SavingGoalRepository savingGoalRepository;

    @Mock
    private GoalContributionRepository goalContributionRepository;

    @Mock
    private SavingGoalMapper savingGoalMapper;

    @InjectMocks
    private SavingGoalServiceImpl savingGoalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("US06-CP01 - Registro de Meta de Ahorro Exitoso")
    void addSavingGoal_success() {
        // Arrange
        LocalDate dueDate = LocalDate.of(2025, 12, 31);
        AddSavingGoalRequest request = new AddSavingGoalRequest("Viaje a Japón", 1000.0f, 100.0f, dueDate);

        SavingGoal goalEntity = new SavingGoal();
        goalEntity.setName("Viaje a Japón");
        goalEntity.setTargetAmount(1000.0f);
        goalEntity.setDueDate(dueDate);
        goalEntity.setCurrentAmount(0.0f);

        SavingGoal savedGoal = new SavingGoal();
        savedGoal.setName("Viaje a Japón");
        savedGoal.setTargetAmount(1000.0f);
        savedGoal.setDueDate(dueDate);
        savedGoal.setCurrentAmount(0.0f);

        AddSavingGoalResponse expectedResponse = new AddSavingGoalResponse("Viaje a Japón", 1000.0f, dueDate);

        when(savingGoalMapper.toEntity(request)).thenReturn(goalEntity);
        when(savingGoalRepository.save(goalEntity)).thenReturn(savedGoal);
        when(savingGoalMapper.toResponse(savedGoal)).thenReturn(expectedResponse);

        // Act
        AddSavingGoalResponse actualResponse = savingGoalService.addSavingGoal(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("Viaje a Japón", actualResponse.name());
        assertEquals(1000.0f, actualResponse.targetAmount());
        assertEquals(dueDate, actualResponse.dueDate());

        verify(savingGoalMapper).toEntity(request);
        verify(savingGoalRepository).save(goalEntity);
        verify(savingGoalMapper).toResponse(savedGoal);
    }

    @Test
    @DisplayName("US06-CP02 - Falla si el monto objetivo es menor al monto actual")
    void addSavingGoal_targetLessThanCurrent_throwsException() {
        // Arrange
        AddSavingGoalRequest request = new AddSavingGoalRequest(
                "Comprar laptop", 500.0f, 600.0f, LocalDate.of(2025, 10, 1)
        );

        // Act & Assert
        assertThrows(TargetAmountLessThanCurrentAmountException.class, () -> {
            savingGoalService.addSavingGoal(request);
        });

        verify(savingGoalMapper, never()).toEntity(any());
        verify(savingGoalRepository, never()).save(any());
        verify(savingGoalMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("US06-CP04 - Error si la meta no existe")
    void addSavingGoal_goalNotFound_error() {
        when(savingGoalRepository.findByName("MetaInexistente")).thenReturn(Optional.empty());

        AddSavingGoalRequest request = new AddSavingGoalRequest("MetaInexistente", 1000.0f, 100.0f, LocalDate.now());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                savingGoalService.addSavingGoal(request)
        );

        assertEquals("Meta no encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("US06-CP03 - Error si el monto objetivo es cero")
    void addSavingGoal_zeroAmount_error() {
        AddSavingGoalRequest request = new AddSavingGoalRequest("CoronelLeoncioPrado", 1000.0f, 1000.0f, LocalDate.now());

        TargetAmountLessThanCurrentAmountException exception = assertThrows(
                TargetAmountLessThanCurrentAmountException.class,
                () -> savingGoalService.addSavingGoal(request)
        );

        assertEquals("Target amount less than current amount", exception.getMessage());
    }

    @Test
    @DisplayName("US06-CP05 - Error si la fecha es nula o pasada")
    void addSavingGoal_invalidDate_error() {

        SavingGoal goalMock = new SavingGoal();
        when(savingGoalMapper.toEntity(any())).thenReturn(goalMock);

        AddSavingGoalRequest requestNullDate = new AddSavingGoalRequest(
                "Meta sin fecha", 1000.0f, 100.0f, null
        );
        AddSavingGoalRequest requestPastDate = new AddSavingGoalRequest(
                "Meta fecha pasada", 1000.0f, 100.0f, LocalDate.now().minusDays(1)
        );

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                savingGoalService.addSavingGoal(requestNullDate)
        );
        assertEquals("La fecha de vencimiento debe ser hoy o futura", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                savingGoalService.addSavingGoal(requestPastDate)
        );
        assertEquals("La fecha de vencimiento debe ser hoy o futura", ex2.getMessage());

        verify(savingGoalMapper, never()).toEntity(any());
        verify(savingGoalRepository, never()).save(any());
    }
}