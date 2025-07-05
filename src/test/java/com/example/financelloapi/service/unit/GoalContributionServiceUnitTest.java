package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.RegisterGoalContributionRequest;
import com.example.financelloapi.dto.test.RegisterGoalContributionResponse;
import com.example.financelloapi.exception.ContributionExceedsTargetException;
import com.example.financelloapi.exception.EmptyAmountException;
import com.example.financelloapi.mapper.GoalContributionMapper;
import com.example.financelloapi.repository.GoalContributionRepository;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.model.entity.GoalContribution;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.model.enums.SavingGoalProgress;
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

        SavingGoal savingGoal = new SavingGoal();
        savingGoal.setId(1);
        savingGoal.setCurrentAmount(200.0f);
        savingGoal.setTargetAmount(1000.0f);
        savingGoal.setProgress(SavingGoalProgress.IN_PROGRESS);

        GoalContribution goalContribution = new GoalContribution();
        goalContribution.setId(1);
        goalContribution.setAmount(150.0f);
        goalContribution.setDate(request.date());
        goalContribution.setSavingGoal(savingGoal);

        RegisterGoalContributionResponse expectedResponse = new RegisterGoalContributionResponse(
                150.0f, request.date()
        );

        when(savingGoalRepository.findById(1)).thenReturn(Optional.of(savingGoal));
        when(goalContributionRepository.save(any(GoalContribution.class))).thenReturn(goalContribution);
        when(savingGoalRepository.save(any(SavingGoal.class))).thenReturn(savingGoal);
        when(goalContributionMapper.toResponse(any(GoalContribution.class))).thenReturn(expectedResponse);

        RegisterGoalContributionResponse actualResponse = goalContributionService.registerGoalContribution(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.amount(), actualResponse.amount());
        assertEquals(expectedResponse.date(), actualResponse.date());

        // Verificar que el current_amount se actualizó correctamente
        assertEquals(350.0f, savingGoal.getCurrentAmount()); // 200 + 150

        verify(savingGoalRepository).findById(1);
        verify(goalContributionRepository).save(any(GoalContribution.class));
        verify(savingGoalRepository).save(savingGoal);
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
    @DisplayName("US05-CP03 - Error al registrar aporte que excede la meta objetivo")
    void registerGoalContribution_exceedsTarget_throwsException() {
        RegisterGoalContributionRequest request = new RegisterGoalContributionRequest(1, 600.0f, LocalDate.now());

        SavingGoal savingGoal = new SavingGoal();
        savingGoal.setId(1);
        savingGoal.setCurrentAmount(500.0f);
        savingGoal.setTargetAmount(1000.0f);
        savingGoal.setProgress(SavingGoalProgress.IN_PROGRESS);

        when(savingGoalRepository.findById(1)).thenReturn(Optional.of(savingGoal));

        ContributionExceedsTargetException exception = assertThrows(ContributionExceedsTargetException.class, () -> {
            goalContributionService.registerGoalContribution(request);
        });

        assertTrue(exception.getMessage().contains("Esta contribución de $600.0"));
        assertTrue(exception.getMessage().contains("Meta objetivo: $1000.0"));
        assertTrue(exception.getMessage().contains("Monto actual: $500.0"));

        verify(savingGoalRepository).findById(1);
        verify(goalContributionRepository, never()).save(any(GoalContribution.class));
        verify(savingGoalRepository, never()).save(any(SavingGoal.class));
    }

    @Test
    @DisplayName("US05-CP04 - Registrar aporte que completa exactamente la meta")
    void registerGoalContribution_completesGoal_setsProgressToDone() {
        RegisterGoalContributionRequest request = new RegisterGoalContributionRequest(1, 300.0f, LocalDate.now());

        SavingGoal savingGoal = new SavingGoal();
        savingGoal.setId(1);
        savingGoal.setCurrentAmount(700.0f);
        savingGoal.setTargetAmount(1000.0f);
        savingGoal.setProgress(SavingGoalProgress.IN_PROGRESS);

        GoalContribution goalContribution = new GoalContribution();
        goalContribution.setId(1);
        goalContribution.setAmount(300.0f);
        goalContribution.setDate(request.date());
        goalContribution.setSavingGoal(savingGoal);

        RegisterGoalContributionResponse expectedResponse = new RegisterGoalContributionResponse(
                300.0f, request.date()
        );

        when(savingGoalRepository.findById(1)).thenReturn(Optional.of(savingGoal));
        when(goalContributionRepository.save(any(GoalContribution.class))).thenReturn(goalContribution);
        when(savingGoalRepository.save(any(SavingGoal.class))).thenReturn(savingGoal);
        when(goalContributionMapper.toResponse(any(GoalContribution.class))).thenReturn(expectedResponse);

        RegisterGoalContributionResponse actualResponse = goalContributionService.registerGoalContribution(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.amount(), actualResponse.amount());
        assertEquals(expectedResponse.date(), actualResponse.date());

        // Verificar que el current_amount se actualizó correctamente
        assertEquals(1000.0f, savingGoal.getCurrentAmount()); // 700 + 300
        // Verificar que el progress cambió a DONE
        assertEquals(SavingGoalProgress.DONE, savingGoal.getProgress());

        verify(savingGoalRepository).findById(1);
        verify(goalContributionRepository).save(any(GoalContribution.class));
        verify(savingGoalRepository).save(savingGoal);
        verify(goalContributionMapper).toResponse(any(GoalContribution.class));
    }

    @Test
    @DisplayName("US05-CP05 - Error al registrar aporte para meta no encontrada")
    void registerGoalContribution_goalNotFound_throwsException() {
        RegisterGoalContributionRequest request = new RegisterGoalContributionRequest(999, 150.0f, LocalDate.now());

        when(savingGoalRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            goalContributionService.registerGoalContribution(request);
        });

        assertEquals("Goal not found", exception.getMessage());

        verify(savingGoalRepository).findById(999);
        verify(goalContributionRepository, never()).save(any(GoalContribution.class));
        verify(savingGoalRepository, never()).save(any(SavingGoal.class));
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