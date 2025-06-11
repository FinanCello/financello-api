package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.exception.EmptyAmountException;
import com.example.financelloapi.mapper.GoalContributionMapper;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.service.impl.GoalContributionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GoalContributionServiceUnitTest {

    @Mock
    private GoalContributionRepository goalContributionRepository;

    @Mock
    private GoalContributionMapper goalContributionMapper;

    @Mock
    private SavingGoalRepository savingGoalRepository;

    @InjectMocks
    private GoalContributionServiceImpl goalContributionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("US05-CP01 - Registrar aporte correctamente")
    void registerGoalContribution_success() {
        RegisterGoalContributionRequest request = new RegisterGoalContributionRequest(1, 150.0f, LocalDate.now());

        when(savingGoalRepository.existsById(1)).thenReturn(true);

        SavingGoal goal = new SavingGoal();
        goal.setId(1);
        when(savingGoalRepository.getById(1)).thenReturn(goal);

        GoalContribution contributionToSave = new GoalContribution();
        contributionToSave.setAmount(request.amount());
        contributionToSave.setDate(request.date());
        contributionToSave.setSavingGoal(goal);
        when(goalContributionRepository.save(any(GoalContribution.class))).thenReturn(contributionToSave);

        RegisterGoalContributionResponse response = new RegisterGoalContributionResponse(request.amount(), request.date());
        when(goalContributionMapper.toResponse(any(GoalContribution.class))).thenReturn(response);

        RegisterGoalContributionResponse result = goalContributionService.registerGoalContribution(request);

        assertNotNull(result);
        assertEquals(request.amount(), result.amount());
        assertEquals(request.date(), result.date());

        verify(savingGoalRepository).existsById(1);
        verify(savingGoalRepository).getById(1);
        verify(goalContributionRepository).save(any(GoalContribution.class));
        verify(goalContributionMapper).toResponse(any(GoalContribution.class));
    }

    @Test
    @DisplayName("US05-CP02 - Error al registrar aporte con monto cero o negativo")
    void registerGoalContribution_invalidAmount_throwsException() {
        RegisterGoalContributionRequest request = new RegisterGoalContributionRequest(1, 0.0f, LocalDate.now());

        assertThrows(EmptyAmountException.class, () -> {
            goalContributionService.registerGoalContribution(request);
        });

        verifyNoInteractions(savingGoalRepository);
        verifyNoInteractions(goalContributionRepository);
        verifyNoInteractions(goalContributionMapper);
    }

    @Test
    @DisplayName("US05-CP03 - Error al registrar aporte con meta inexistente")
    void registerGoalContribution_nonexistentGoal_throwsException() {
        RegisterGoalContributionRequest request = new RegisterGoalContributionRequest(99, 100.0f, LocalDate.now());

        when(savingGoalRepository.existsById(99)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            goalContributionService.registerGoalContribution(request);
        });

        assertEquals("Goal not found", ex.getMessage());

        verify(savingGoalRepository).existsById(99);
        verifyNoMoreInteractions(savingGoalRepository);
        verifyNoInteractions(goalContributionRepository);
        verifyNoInteractions(goalContributionMapper);
    }

    @Test
    @DisplayName("US11-CP01 - Obtener historial de aportes")
    void historyGoalContributions_returnsList() {
        GoalContribution c1 = new GoalContribution();
        c1.setAmount(100.0f);
        c1.setDate(LocalDate.of(2024, 6, 1));

        GoalContribution c2 = new GoalContribution();
        c2.setAmount(200.0f);
        c2.setDate(LocalDate.of(2024, 6, 2));

        when(goalContributionRepository.findAll()).thenReturn(List.of(c1, c2));

        List<GoalContribution> result = goalContributionService.historyGoalContributions();

        // Aserciones
        assertEquals(2, result.size());                 // <-- Aquí da error (espera 2 pero recibe 0)
        assertEquals(100.0f, result.get(0).getAmount());
        assertEquals(LocalDate.of(2024, 6, 1), result.get(0).getDate());
    }

    @Test
    @DisplayName("US11-CP02 - Obtener historial vacío cuando no hay aportes")
    void historyGoalContributions_returnsEmptyList() {
        when(goalContributionRepository.findAll()).thenReturn(List.of());

        List<GoalContribution> result = goalContributionService.historyGoalContributions();

        assertTrue(result.isEmpty());
    }
}