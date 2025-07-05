package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.AddSavingGoalRequest;
import com.example.financelloapi.dto.request.UpdateSavingGoalRequest;
import com.example.financelloapi.dto.test.AddSavingGoalResponse;
import com.example.financelloapi.exception.SavingGoalHasContributionsException;
import com.example.financelloapi.exception.TargetAmountLessThanCurrentAmountException;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.mapper.SavingGoalMapper;
import com.example.financelloapi.model.entity.SavingGoal;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.enums.SavingGoalProgress;
import com.example.financelloapi.repository.SavingGoalRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.impl.SavingGoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SavingGoalServiceUnitTest {

    @Mock
    private SavingGoalRepository savingGoalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SavingGoalMapper savingGoalMapper;

    @InjectMocks
    private SavingGoalServiceImpl savingGoalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("US06-CP01 - Registro de Meta de Ahorro Exitoso")
    void addSavingGoal_success() {
        // Arrange
        Integer userId = 1;
        LocalDate dueDate = LocalDate.of(2025, 12, 31);
        AddSavingGoalRequest request = new AddSavingGoalRequest("Viaje a Japón", 1000.0f, dueDate);

        User user = new User();
        user.setId(userId);

        SavingGoal goalToSave = new SavingGoal();
        goalToSave.setName("Viaje a Japón");
        goalToSave.setTargetAmount(1000.0f);
        goalToSave.setDueDate(dueDate);
        goalToSave.setCurrentAmount(0.0f);
        goalToSave.setUser(user);

        SavingGoal savedGoal = new SavingGoal();
        savedGoal.setId(1);
        savedGoal.setName("Viaje a Japón");
        savedGoal.setTargetAmount(1000.0f);
        savedGoal.setDueDate(dueDate);
        savedGoal.setCurrentAmount(0.0f);
        savedGoal.setUser(user);

        AddSavingGoalResponse expectedResponse = new AddSavingGoalResponse(
                1, "Viaje a Japón", 1000.0f, 0.0f, dueDate, userId, SavingGoalProgress.IN_PROGRESS
        );

        when(userRepository.findByIdCustom(userId)).thenReturn(Optional.of(user));
        when(savingGoalRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(savingGoalMapper.toEntity(request, user)).thenReturn(goalToSave);
        when(savingGoalRepository.save(any(SavingGoal.class))).thenReturn(savedGoal);
        when(savingGoalMapper.toResponse(savedGoal)).thenReturn(expectedResponse);

        // Act
        AddSavingGoalResponse actualResponse = savingGoalService.addSavingGoal(userId, request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.targetAmount(), actualResponse.targetAmount());
        assertEquals(expectedResponse.currentAmount(), actualResponse.currentAmount());
        assertEquals(expectedResponse.dueDate(), actualResponse.dueDate());
        assertEquals(expectedResponse.userId(), actualResponse.userId());
        assertEquals(expectedResponse.progress(), actualResponse.progress());
        assertEquals(SavingGoalProgress.IN_PROGRESS, actualResponse.progress());

        verify(userRepository).findByIdCustom(userId);
        verify(savingGoalRepository).findByName(request.name());
        verify(savingGoalMapper).toEntity(request, user);
        verify(savingGoalRepository).save(any(SavingGoal.class));
        verify(savingGoalMapper).toResponse(savedGoal);
    }

    @Test
    @DisplayName("US06-CP02 - Usuario no encontrado")
    void addSavingGoal_userNotFound_throwsException() {
        // Arrange
        Integer userId = 999;
        LocalDate dueDate = LocalDate.of(2025, 12, 31);
        AddSavingGoalRequest request = new AddSavingGoalRequest("Viaje a Japón", 1000.0f, dueDate);

        when(userRepository.findByIdCustom(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserDoesntExistException.class, () -> {
            savingGoalService.addSavingGoal(userId, request);
        });

        verify(userRepository).findByIdCustom(userId);
        verify(savingGoalRepository, never()).findByName(anyString());
        verify(savingGoalRepository, never()).save(any());
    }

    @Test
    @DisplayName("US16-CP01 - Obtener metas por usuario - exitoso")
    void getGoalsByUser_success() {
        // Arrange
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        SavingGoal goal1 = new SavingGoal();
        goal1.setId(1);
        goal1.setName("Meta 1");
        goal1.setTargetAmount(1000f);
        goal1.setCurrentAmount(200f);
        goal1.setUser(user);

        SavingGoal goal2 = new SavingGoal();
        goal2.setId(2);
        goal2.setName("Meta 2");
        goal2.setTargetAmount(500f);
        goal2.setCurrentAmount(100f);
        goal2.setUser(user);

        List<SavingGoal> goals = List.of(goal1, goal2);

        AddSavingGoalResponse response1 = new AddSavingGoalResponse(
                1, "Meta 1", 1000f, 200f, LocalDate.now().plusDays(30), userId, SavingGoalProgress.IN_PROGRESS
        );
        AddSavingGoalResponse response2 = new AddSavingGoalResponse(
                2, "Meta 2", 500f, 100f, LocalDate.now().plusDays(60), userId, SavingGoalProgress.IN_PROGRESS
        );

        when(userRepository.findByIdCustom(userId)).thenReturn(Optional.of(user));
        when(savingGoalRepository.findByUserId(userId)).thenReturn(goals);
        when(savingGoalMapper.toResponse(goal1)).thenReturn(response1);
        when(savingGoalMapper.toResponse(goal2)).thenReturn(response2);

        // Act
        List<AddSavingGoalResponse> result = savingGoalService.getGoalsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(userRepository).findByIdCustom(userId);
        verify(savingGoalRepository).findByUserId(userId);
        verify(savingGoalMapper).toResponse(goal1);
        verify(savingGoalMapper).toResponse(goal2);
    }

    @Test
    @DisplayName("US16-CP02 - Usuario no encontrado al obtener metas")
    void getGoalsByUser_userNotFound_throwsException() {
        // Arrange
        Integer userId = 999;
        when(userRepository.findByIdCustom(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserDoesntExistException.class, () -> {
            savingGoalService.getGoalsByUser(userId);
        });

        verify(userRepository).findByIdCustom(userId);
        verify(savingGoalRepository, never()).findByUserId(anyInt());
    }

    @Test
    @DisplayName("US14-CP01 - Edición de meta de ahorro")
    void whenUpdateWithValidData_thenReturnsUpdatedResponse() {
        // Arrange
        Integer goalId = 1;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(100f);

        User user = new User();
        user.setId(1);
        existing.setUser(user);

        UpdateSavingGoalRequest req = new UpdateSavingGoalRequest("Viaje",200f, LocalDate.now().plusDays(5));
        AddSavingGoalResponse expectedResponse = new AddSavingGoalResponse(
                goalId, "Meta Test", req.targetAmount(), 100f, req.dueDate(), 1, SavingGoalProgress.IN_PROGRESS
        );

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));
        when(savingGoalRepository.save(existing)).thenReturn(existing);
        when(savingGoalMapper.toResponse(existing)).thenReturn(expectedResponse);

        // Act
        AddSavingGoalResponse actual = savingGoalService.updateSavingGoal(goalId, req);

        // Assert
        assertEquals(expectedResponse, actual);
        verify(savingGoalRepository).save(existing);
    }

    @Test
    @DisplayName("US15-CP01 - Eliminación de meta de ahorro exitosa")
    void whenDeleteWithNoContributions_thenDeletesSuccessfully() {
        // Arrange
        Integer goalId = 10;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(0.0f);

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // Act
        assertDoesNotThrow(() -> savingGoalService.deleteSavingGoal(goalId));

        // Assert
        verify(savingGoalRepository).delete(existing);
    }

    @Test
    @DisplayName("US15-CP02 - Error al eliminar meta con aportes registrados")
    void whenDeleteWithContributions_thenThrowsSavingGoalHasContributionsException() {
        // Arrange
        Integer goalId = 11;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(50.0f);

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // Act & Assert
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
    @DisplayName("US14-CP03 - Error al editar meta con target_amount menor al current_amount")
    void updateSavingGoal_targetLessThanCurrent_throwsException() {
        // Arrange
        Integer goalId = 1;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(500f); // Ya tiene $500 acumulados
        existing.setTargetAmount(1000f);

        UpdateSavingGoalRequest request = new UpdateSavingGoalRequest(
                "Viaje",300f, // Intentar reducir a $300 (menor que los $500 actuales)
                LocalDate.now().plusMonths(6)
        );

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // Act & Assert
        TargetAmountLessThanCurrentAmountException exception = assertThrows(
            TargetAmountLessThanCurrentAmountException.class, 
            () -> savingGoalService.updateSavingGoal(goalId, request)
        );

        assertTrue(exception.getMessage().contains("No se puede reducir la meta objetivo a $300.0"));
        assertTrue(exception.getMessage().contains("ya se han acumulado $500.0"));

        verify(savingGoalRepository).findById(goalId);
        verify(savingGoalRepository, never()).save(any(SavingGoal.class));
    }

    @Test
    @DisplayName("US14-CP04 - Error al editar meta con target_amount cero o negativo")
    void updateSavingGoal_invalidTargetAmount_throwsException() {
        // Arrange
        Integer goalId = 1;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(100f);

        UpdateSavingGoalRequest request = new UpdateSavingGoalRequest(
                "Viaje",0f, // Monto inválido
                LocalDate.now().plusMonths(6)
        );

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> savingGoalService.updateSavingGoal(goalId, request)
        );

        assertEquals("El monto objetivo debe ser mayor a 0", exception.getMessage());

        verify(savingGoalRepository).findById(goalId);
        verify(savingGoalRepository, never()).save(any(SavingGoal.class));
    }

    @Test
    @DisplayName("US14-CP05 - Error al editar meta con fecha pasada")
    void updateSavingGoal_pastDate_throwsException() {
        // Arrange
        Integer goalId = 1;
        SavingGoal existing = new SavingGoal();
        existing.setId(goalId);
        existing.setCurrentAmount(100f);

        UpdateSavingGoalRequest request = new UpdateSavingGoalRequest(
                "Viaje",1000f,
                LocalDate.now().minusDays(1) // Fecha pasada
        );

        when(savingGoalRepository.findById(goalId)).thenReturn(Optional.of(existing));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> savingGoalService.updateSavingGoal(goalId, request)
        );

        assertEquals("La fecha de vencimiento debe ser hoy o futura", exception.getMessage());

        verify(savingGoalRepository).findById(goalId);
        verify(savingGoalRepository, never()).save(any(SavingGoal.class));
    }
}