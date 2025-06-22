package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.exception.SavingGoalHasContributionsException;
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

        LocalDate dueDate = LocalDate.of(2025, 12, 31);
        AddSavingGoalRequest request = new AddSavingGoalRequest("Viaje a Japón", 1000.0f, 100.0f, dueDate);

        SavingGoal goalToSave = new SavingGoal();
        goalToSave.setName("Viaje a Japón");
        goalToSave.setTargetAmount(1000.0f);
        goalToSave.setDueDate(dueDate);
        goalToSave.setCurrentAmount(0.0f);

        SavingGoal savedGoal = new SavingGoal();
        savedGoal.setName("Viaje a Japón");
        savedGoal.setTargetAmount(1000.0f);
        savedGoal.setDueDate(dueDate);
        savedGoal.setCurrentAmount(0.0f);

        AddSavingGoalResponse expectedResponse = new AddSavingGoalResponse("Viaje a Japón", 1000.0f, dueDate);

        when(savingGoalRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(savingGoalRepository.save(any(SavingGoal.class))).thenReturn(savedGoal);
        when(savingGoalMapper.toResponse(savedGoal)).thenReturn(expectedResponse);

        AddSavingGoalResponse actualResponse = savingGoalService.addSavingGoal(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.targetAmount(), actualResponse.targetAmount());
        assertEquals(expectedResponse.dueDate(), actualResponse.dueDate());

        verify(savingGoalRepository).findByName(request.name());
        verify(savingGoalRepository).save(any(SavingGoal.class));
        verify(savingGoalMapper).toResponse(savedGoal);
    }


    @Test
    @DisplayName("US06-CP02 - Falla si el monto objetivo es menor al monto actual")
    void addSavingGoal_targetLessThanCurrent_throwsException() {
        // Arrange
        AddSavingGoalRequest request = new AddSavingGoalRequest(
                "Comprar laptop", 500.0f, 600.0f, LocalDate.of(2025, 10, 1)
        );

        SavingGoal existingGoal = new SavingGoal();
        existingGoal.setName("Comprar laptop");
        existingGoal.setCurrentAmount(600.0f);  // mayor que el targetAmount 500.0f

        when(savingGoalRepository.findByName("Comprar laptop")).thenReturn(Optional.of(existingGoal));

        // Act & Assert
        assertThrows(TargetAmountLessThanCurrentAmountException.class, () -> {
            savingGoalService.addSavingGoal(request);
        });

        verify(savingGoalMapper, never()).toEntity(any());
        verify(savingGoalRepository, never()).save(any());
        verify(savingGoalMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("US06-CP03 - Error si el monto objetivo es cero")
    void addSavingGoal_zeroAmount_error() {
        AddSavingGoalRequest request = new AddSavingGoalRequest("CoronelLeoncioPrado", 0.0f, 0.0f, LocalDate.now());

        SavingGoal existingGoal = new SavingGoal();
        existingGoal.setName("CoronelLeoncioPrado");
        existingGoal.setCurrentAmount(0.0f);

        when(savingGoalRepository.findByName("CoronelLeoncioPrado")).thenReturn(Optional.of(existingGoal));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savingGoalService.addSavingGoal(request)
        );

        assertEquals("El monto debe ser mayor a 0", exception.getMessage());
    }


    @Test
    @DisplayName("US06-CP04 - Error si la fecha es nula o pasada")
    void addSavingGoal_invalidDate_error() {

        SavingGoal existingGoalNullDate = new SavingGoal();
        existingGoalNullDate.setName("Meta sin fecha");
        existingGoalNullDate.setCurrentAmount(0.0f);
        when(savingGoalRepository.findByName("Meta sin fecha")).thenReturn(Optional.of(existingGoalNullDate));

        SavingGoal existingGoalPastDate = new SavingGoal();
        existingGoalPastDate.setName("Meta fecha pasada");
        existingGoalPastDate.setCurrentAmount(0.0f);
        when(savingGoalRepository.findByName("Meta fecha pasada")).thenReturn(Optional.of(existingGoalPastDate));

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

    @Test
    @DisplayName("US14-CP01 - Edición de meta de ahorro")
    void whenUpdateWithValidData_thenReturnsUpdatedResponse() {
        // dado
        Integer goalId = 1;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(100f);

        UpdateSavingGoalRequest req = new UpdateSavingGoalRequest(200f, LocalDate.now().plusDays(5));
        // Asegúrate de convertir el ID a String si tu DTO lo pide así
        AddSavingGoalResponse expectedResponse = new AddSavingGoalResponse(
                String.valueOf(goalId),
                req.targetAmount(),
                req.dueDate()
        );

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));
        when(savingGoalRepository.save(existing)).thenReturn(existing);
        when(savingGoalMapper.toResponse(existing)).thenReturn(expectedResponse);

        // cuando
        AddSavingGoalResponse actual = savingGoalService.updateSavingGoal(goalId, req);

        // entonces
        assertEquals(expectedResponse, actual);
        verify(savingGoalRepository).save(existing);
    }

    @Test
    @DisplayName("US14-CP02 - Fecha no válida")
    void whenUpdateWithPastDate_thenThrowsIllegalArgumentException() {
        // dado
        Integer goalId = 2;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(0f);

        // ¡Fíjate en el nombre correcto aquí!
        UpdateSavingGoalRequest req = new UpdateSavingGoalRequest(
                100f,
                LocalDate.now().minusDays(1)
        );

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // cuando / entonces
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> savingGoalService.updateSavingGoal(goalId, req)
        );
        assertEquals("La fecha de vencimiento debe ser hoy o futura", ex.getMessage());
        verify(savingGoalRepository, never()).save(any());
    }

    @Test
    @DisplayName("US14-CP03 - Meta no encontrada")

    void whenUpdateNonExistingGoal_thenThrowsNoSuchElementException() {
        // dado
        Integer goalId = 3;
        UpdateSavingGoalRequest req = new UpdateSavingGoalRequest(
                100f,
                LocalDate.now().plusDays(1)
        );

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.empty());

        // cuando / entonces
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> savingGoalService.updateSavingGoal(goalId, req)
        );
        assertEquals("Meta no encontrada", ex.getMessage());
        verify(savingGoalRepository, never()).save(any());
    }

    @Test
    @DisplayName("US15-CP01 - Eliminación de meta de ahorro exitosa")
    void whenDeleteWithNoContributions_thenDeletesSuccessfully() {
        // dado
        Integer goalId = 10;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(0.0f);

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // cuando
        assertDoesNotThrow(() -> savingGoalService.deleteSavingGoal(goalId));

        // entonces
        verify(savingGoalRepository).delete(existing);
    }

    @Test
    @DisplayName("US15-CP02 - Error al eliminar meta con aportes registrados")
    void whenDeleteWithContributions_thenThrowsSavingGoalHasContributionsException() {
        // dado
        Integer goalId = 11;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(50.0f);

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // cuando / entonces
        SavingGoalHasContributionsException ex = assertThrows(
                SavingGoalHasContributionsException.class,
                () -> savingGoalService.deleteSavingGoal(goalId)
        );
        assertEquals(
                "No se puede eliminar la meta " + goalId + ": tiene aportes registrados",
                ex.getMessage()
        );
        verify(savingGoalRepository, never()).delete(any());
    }

    @Test
    @DisplayName("US15-CP03 - Meta no existe")
    void whenDeleteNonExistingGoal_thenThrowsIllegalArgumentException() {
        // dado
        Integer goalId = 12;
        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.empty());

        // cuando / entonces
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> savingGoalService.deleteSavingGoal(goalId)
        );
        assertTrue(
                ex.getMessage().contains("Meta no encontrada"),
                "Se esperaba mensaje de 'Meta no encontrada', pero fue: " + ex.getMessage()
        );
        verify(savingGoalRepository, never()).delete(any());
    }

}