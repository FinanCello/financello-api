package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.exception.CategoryNotFoundException;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.mapper.FinancialMovementMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.enums.CurrencyType;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.impl.FinancialMovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDate;
import java.util.Optional;

public class FinancialMovementServiceUnitTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FinancialMovementRepository financialMovementRepository;
    @Mock
    private FinancialMovementMapper financialMovementMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FinancialMovementServiceImpl financialMovementService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // US03: Registrar Movimiento Financiero
    @Test
    @DisplayName("US03-CP01 - Registro de Movimiento Financiero Exitoso")
    void registerMovement_success() {
        // Arrange
        Integer userId = 1;
        LocalDate date = LocalDate.of(2024, 6, 5);

        RegisterFinancialMovementRequest request = new RegisterFinancialMovementRequest(100.0f, date, MovementType.INCOME, 10, CurrencyType.USD);

        Category category = new Category();
        category.setId(10);
        category.setName("Salary");
        category.setDescription("Monthly income");

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        FinancialMovement movement = new FinancialMovement();
        movement.setAmount(request.amount());
        movement.setDate(request.date());
        movement.setMovementType(request.movementType());
        movement.setCurrencyType(request.currencyType());
        movement.setCategory(category);
        movement.setUser(user);

        RegisterFinancialMovementResponse expectedResponse = new RegisterFinancialMovementResponse(
                request.amount(),
                request.date(),
                request.movementType(),
                new CategoryResponse(category.getName(), category.getDescription()),
                request.currencyType()
        );

        when(categoryRepository.findById(10)).thenReturn(Optional.of(category));
        when(userRepository.getById(userId)).thenReturn(user);
        when(financialMovementMapper.toEntity(request, category)).thenReturn(movement);
        when(financialMovementMapper.toRegisterFinancialMovementResponse(movement)).thenReturn(expectedResponse);

        // Act
        RegisterFinancialMovementResponse response = financialMovementService.registerMovement(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.amount(), response.amount());
        assertEquals(expectedResponse.categoryResponse().name(), response.categoryResponse().name());
        verify(financialMovementRepository).save(movement);
    }

    @Test
    @DisplayName("US03-CP02 - Registro de Movimiento Financiero: Monto menor o igual a 0")
    void registerMovement_fails_whenAmountInvalid() {
        // Arrange
        RegisterFinancialMovementRequest request = new RegisterFinancialMovementRequest(
                0.0f,
                LocalDate.now(),
                MovementType.OUTCOME,
                1,
                CurrencyType.PEN
        );

        // Act & Assert
        assertThrows(CustomException.class, () -> financialMovementService.registerMovement(1, request));
        verify(financialMovementRepository, never()).save(any());
    }

    @Test
    @DisplayName("US03-CP03 - Registro de Movimiento Financiero: Categoría no encontrada")
    void registerMovement_fails_whenCategoryNotFound() {
        // Arrange
        RegisterFinancialMovementRequest request = new RegisterFinancialMovementRequest(
                150.0f,
                LocalDate.now(),
                MovementType.OUTCOME,
                99,
                CurrencyType.PEN
        );

        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> financialMovementService.registerMovement(1, request));
        verify(financialMovementRepository, never()).save(any());
    }

}
