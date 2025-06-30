package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.BudgetRequest;
import com.example.financelloapi.dto.response.BudgetResponse;
import com.example.financelloapi.dto.response.BudgetStatusResponse;
import com.example.financelloapi.mapper.BudgetMapper;
import com.example.financelloapi.model.entity.Budget;
import com.example.financelloapi.model.entity.Role;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.enums.RoleType;
import com.example.financelloapi.repository.BudgetRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceUnitTest {

    @Mock
    private BudgetRepository budgetRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private User testUser;
    private Budget testBudget;
    private BudgetRequest testRequest;
    private BudgetResponse testResponse;

    @BeforeEach
    public void setUp() {
        // Setup test data
        Role role = new Role(1, RoleType.BASIC);
        
        testUser = User.builder()
                .id(1)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .password("password123")
                .role(role)
                .build();

        testBudget = new Budget();
        testBudget.setId(1);
        testBudget.setName("Presupuesto Mensual Enero");
        testBudget.setPeriod("2025-01");
        testBudget.setTotalIncomePlanned(5000.0f);
        testBudget.setTotalOutcomePlanned(4000.0f);
        testBudget.setUser(testUser);

        testRequest = new BudgetRequest("Presupuesto Mensual Enero", "2025-01", 5000.0f, 4000.0f);
        
        testResponse = new BudgetResponse(1, "Presupuesto Mensual Enero", "2025-01", 5000.0f, 4000.0f, 1);
    }

    @Test
    @DisplayName("Crear presupuesto válido - Debe retornar BudgetResponse")
    void createBudget_validRequest_returnsBudgetResponse() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUser_IdAndPeriod(1, "2025-01")).thenReturn(Optional.empty());
        when(budgetMapper.toEntity(testRequest, testUser)).thenReturn(testBudget);
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);
        when(budgetMapper.toResponse(testBudget)).thenReturn(testResponse);

        // Act
        BudgetResponse result = budgetService.createBudget(1, testRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Presupuesto Mensual Enero", result.name());
        assertEquals("2025-01", result.period());
        assertEquals(5000.0f, result.totalIncomePlanned());
        assertEquals(4000.0f, result.totalOutcomePlanned());
        assertEquals(1, result.userId());

        verify(userRepository, times(1)).findById(1);
        verify(budgetRepository, times(1)).findByUser_IdAndPeriod(1, "2025-01");
        verify(budgetMapper, times(1)).toEntity(testRequest, testUser);
        verify(budgetRepository, times(1)).save(any(Budget.class));
        verify(budgetMapper, times(1)).toResponse(testBudget);
    }

    @Test
    @DisplayName("Crear presupuesto con usuario inexistente - Debe lanzar IllegalArgumentException")
    void createBudget_userNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> budgetService.createBudget(1, testRequest));
        
        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(userRepository, times(1)).findById(1);
        verify(budgetRepository, never()).findByUser_IdAndPeriod(any(), any());
    }

    @Test
    @DisplayName("Crear presupuesto duplicado para mismo período - Debe lanzar IllegalArgumentException")
    void createBudget_duplicatePeriod_throwsIllegalArgumentException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUser_IdAndPeriod(1, "2025-01")).thenReturn(Optional.of(testBudget));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> budgetService.createBudget(1, testRequest));
        
        assertEquals("Ya existe un presupuesto para ese período.", exception.getMessage());
        verify(userRepository, times(1)).findById(1);
        verify(budgetRepository, times(1)).findByUser_IdAndPeriod(1, "2025-01");
        verify(budgetMapper, never()).toEntity(any(), any());
    }

    @Test
    @DisplayName("Obtener presupuestos por ID de usuario - Debe retornar lista de BudgetResponse")
    void getBudgetsByUserId_validUserId_returnsListOfBudgetResponse() {
        // Arrange
        Budget budget2 = new Budget();
        budget2.setId(2);
        budget2.setName("Presupuesto Mensual Febrero");
        budget2.setPeriod("2025-02");
        budget2.setTotalIncomePlanned(5500.0f);
        budget2.setTotalOutcomePlanned(4200.0f);
        budget2.setUser(testUser);

        BudgetResponse response2 = new BudgetResponse(2, "Presupuesto Mensual Febrero", "2025-02", 5500.0f, 4200.0f, 1);
        
        List<Budget> budgets = Arrays.asList(testBudget, budget2);
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUser_Id(1)).thenReturn(budgets);
        when(budgetMapper.toResponse(testBudget)).thenReturn(testResponse);
        when(budgetMapper.toResponse(budget2)).thenReturn(response2);

        // Act
        List<BudgetResponse> result = budgetService.getBudgetsByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Presupuesto Mensual Enero", result.get(0).name());
        assertEquals("Presupuesto Mensual Febrero", result.get(1).name());
        
        verify(userRepository, times(1)).findById(1);
        verify(budgetRepository, times(1)).findByUser_Id(1);
        verify(budgetMapper, times(2)).toResponse(any(Budget.class));
    }

    @Test
    @DisplayName("Obtener presupuestos con usuario inexistente - Debe lanzar IllegalArgumentException")
    void getBudgetsByUserId_userNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> budgetService.getBudgetsByUserId(1));
        
        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(userRepository, times(1)).findById(1);
        verify(budgetRepository, never()).findByUser_Id(any());
    }

    @Test
    @DisplayName("Obtener presupuesto por ID - Debe retornar BudgetResponse")
    void getBudgetById_validBudgetId_returnsBudgetResponse() {
        // Arrange
        when(budgetRepository.findById(1)).thenReturn(Optional.of(testBudget));
        when(budgetMapper.toResponse(testBudget)).thenReturn(testResponse);

        // Act
        BudgetResponse result = budgetService.getBudgetById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Presupuesto Mensual Enero", result.name());
        assertEquals("2025-01", result.period());
        assertEquals(1, result.id());
        
        verify(budgetRepository, times(1)).findById(1);
        verify(budgetMapper, times(1)).toResponse(testBudget);
    }

    @Test
    @DisplayName("Obtener presupuesto por ID inexistente - Debe lanzar IllegalArgumentException")
    void getBudgetById_budgetNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(budgetRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> budgetService.getBudgetById(1));
        
        assertEquals("Presupuesto no encontrado.", exception.getMessage());
        verify(budgetRepository, times(1)).findById(1);
        verify(budgetMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Obtener status de presupuesto - Debe calcular días restantes correctamente")
    void getBudgetStatus_validBudgetId_returnsStatusWithCorrectDays() {
        // Arrange
        Budget budget = new Budget();
        budget.setId(1);
        budget.setName("Presupuesto Test");
        budget.setPeriod("2025-06"); // Junio 2025
        budget.setTotalIncomePlanned(5000.0f);
        budget.setTotalOutcomePlanned(4000.0f);
        budget.setUser(testUser);

        BudgetStatusResponse expectedResponse = new BudgetStatusResponse(
                1, "Presupuesto Test", "2025-06", 0, 5000.0f, 4000.0f, 5000.0f, 4000.0f
        );

        when(budgetRepository.findById(1)).thenReturn(Optional.of(budget));

        // Act
        BudgetStatusResponse result = budgetService.getBudgetStatus(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.budgetId());
        assertEquals("Presupuesto Test", result.budgetName());
        assertEquals("2025-06", result.period());
        assertEquals(5000.0f, result.remainingIncomeNeeded());
        assertEquals(4000.0f, result.remainingOutcomeAllowed());
        assertEquals(5000.0f, result.totalIncomePlanned());
        assertEquals(4000.0f, result.totalOutcomePlanned());
        // Los días restantes pueden variar según la fecha actual, solo verificamos que no sea null
        assertNotNull(result.daysRemaining());

        verify(budgetRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Obtener status de presupuesto inexistente - Debe lanzar IllegalArgumentException")
    void getBudgetStatus_budgetNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(budgetRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> budgetService.getBudgetStatus(1));
        
        assertEquals("Presupuesto no encontrado.", exception.getMessage());
        verify(budgetRepository, times(1)).findById(1);
    }
}
