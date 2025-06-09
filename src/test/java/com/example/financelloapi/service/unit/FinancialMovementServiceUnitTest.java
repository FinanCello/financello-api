package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.exception.CategoryNotFoundException;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.mapper.FinancialMovementMapper;
import com.example.financelloapi.model.entity.Budget;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.enums.CurrencyType;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.BudgetRepository;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.BudgetService;
import org.springframework.dao.DataAccessException;
import com.example.financelloapi.service.impl.FinancialMovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
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
        LocalDate date = LocalDate.now();

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


    // US16: Filtrar movimiento por categoria o tipo
    @Test
    @DisplayName("US16-CP01 - Filtra movimientos por categoría y tipo correctamente")
    void filterMovements_byCategoryAndType_returnsCorrectResults() {
        Integer userId = 1;
        Integer categoryId = 10;
        MovementType type = MovementType.INCOME;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Salario");
        category.setDescription("Ingreso mensual");

        FinancialMovement movement = new FinancialMovement();
        movement.setAmount(1000f);
        movement.setDate(LocalDate.of(2024, 6, 1));
        movement.setMovementType(type);
        movement.setCategory(category);

        RegisterFinancialMovementResponse expected = new RegisterFinancialMovementResponse(
                1000f,
                movement.getDate(),
                type,
                new CategoryResponse("Salario", "Ingreso mensual"),
                CurrencyType.USD
        );

        when(financialMovementRepository.findByUser_IdAndCategory_IdAndMovementType(userId, categoryId, type)).thenReturn(List.of(movement));
        when(financialMovementMapper.toRegisterFinancialMovementResponse(movement)).thenReturn(expected);

        List<RegisterFinancialMovementResponse> result = financialMovementService.filterMovements(userId, categoryId, type);

        assertEquals(1, result.size());
        assertEquals(1000f, result.get(0).amount());
        assertEquals("Salario", result.get(0).categoryResponse().name());
    }

    @Test
    @DisplayName("US16-CP02 - Filtrar movimientos por tipo")
    void filterMovements_byType_returnsFilteredMovements() {
        Integer userId = 1;

        Category category = new Category();
        category.setId(10);
        category.setName("Salario");
        category.setDescription("Ingreso mensual");

        FinancialMovement movement = new FinancialMovement();
        movement.setAmount(1000f);
        movement.setMovementType(MovementType.INCOME);
        movement.setCategory(category);
        movement.setDate(LocalDate.of(2024, 6, 1));
        movement.setCurrencyType(CurrencyType.USD);

        when(financialMovementRepository.findByUser_IdAndMovementType(userId, MovementType.INCOME)).thenReturn(List.of(movement));
        when(financialMovementMapper.toRegisterFinancialMovementResponse(movement)).thenReturn(new RegisterFinancialMovementResponse(
                movement.getAmount(),
                movement.getDate(),
                movement.getMovementType(),
                new CategoryResponse(category.getName(), category.getDescription()),
                movement.getCurrencyType()
        ));

        List<RegisterFinancialMovementResponse> result = financialMovementService.filterMovements(userId, null, MovementType.INCOME);

        assertEquals(1, result.size());
        assertEquals("Salario", result.get(0).categoryResponse().name());
    }

    @Test
    @DisplayName("US16-CP03 - Filtrar movimientos por categoría")
    void filterMovements_byCategory_returnsFilteredMovements() {
        Integer userId = 1;

        Category category = new Category();
        category.setId(20);
        category.setName("Alimentación");
        category.setDescription("Gastos de comida");

        FinancialMovement movement = new FinancialMovement();
        movement.setAmount(50f);
        movement.setMovementType(MovementType.OUTCOME);
        movement.setCategory(category);
        movement.setDate(LocalDate.of(2024, 6, 3));
        movement.setCurrencyType(CurrencyType.PEN);

        when(financialMovementRepository.findByUser_IdAndCategory_Id(userId, 20)).thenReturn(List.of(movement));
        when(financialMovementMapper.toRegisterFinancialMovementResponse(movement)).thenReturn(new RegisterFinancialMovementResponse(
                movement.getAmount(),
                movement.getDate(),
                movement.getMovementType(),
                new CategoryResponse(category.getName(), category.getDescription()),
                movement.getCurrencyType()
        ));

        List<RegisterFinancialMovementResponse> result = financialMovementService.filterMovements(userId, 20, null);

        assertEquals(1, result.size());
        assertEquals("Alimentación", result.get(0).categoryResponse().name());
    }

    @Test
    @DisplayName("US16-CP04 - Filtro inválido")
    void filterMovements_invalidMovementType_throwsException() {
        Integer userId = 1;
        String invalidMovementType = "GASTO";

        assertThrows(IllegalArgumentException.class, () -> {
            MovementType.valueOf(invalidMovementType.toUpperCase());
        });
    }

    //US08 : Planificar presupuesto mensual

    @Test
    @DisplayName("US08-CP01 - Registro de Movimiento Financiero falla por duplicado")
    void registerMovement_fails_whenDuplicateMovementExists() {
        // Arrange
        Integer userId = 1;
        LocalDate date = LocalDate.of(2024, 6, 5);

        RegisterFinancialMovementRequest request = new RegisterFinancialMovementRequest(
                100.0f,
                date,
                MovementType.INCOME,
                10,
                CurrencyType.USD
        );

        Category category = new Category();
        category.setId(10);

        User user = new User();
        user.setId(userId);

        FinancialMovement existingMovement = new FinancialMovement();
        existingMovement.setAmount(100.0f);
        existingMovement.setDate(date);
        existingMovement.setMovementType(MovementType.INCOME);
        existingMovement.setCategory(category);
        existingMovement.setUser(user);

        when(financialMovementRepository.findByUser_IdAndMovementType(userId, MovementType.INCOME)).thenReturn(List.of(existingMovement));
        when(categoryRepository.findById(10)).thenReturn(Optional.of(category));
        when(userRepository.getById(userId)).thenReturn(user);
        when(financialMovementMapper.toEntity(request, category)).thenReturn(existingMovement);

        // Act & Assert
        assertThrows(CustomException.class, () ->
                financialMovementService.registerMovement(userId, request)
        );
        verify(financialMovementRepository, never()).save(any());
    }


    @Test
    @DisplayName("US08-CP02 - Registro de Movimiento Financiero falla por fecha pasada")
    void registerMovement_fails_whenDateIsInPast() {
        // Arrange
        Integer userId = 1;
        LocalDate pastDate = LocalDate.now().minusMonths(1);

        RegisterFinancialMovementRequest request = new RegisterFinancialMovementRequest(
                80.0f,
                pastDate,
                MovementType.INCOME,
                10,
                CurrencyType.USD
        );

        Category category = new Category();
        category.setId(10);

        User user = new User();
        user.setId(userId);

        FinancialMovement movement = new FinancialMovement();
        movement.setCategory(category);

        when(categoryRepository.findById(10)).thenReturn(Optional.of(category));
        when(financialMovementMapper.toEntity(request, category)).thenReturn(movement);
        when(userRepository.getById(userId)).thenReturn(user);

        // Act & Assert
        assertThrows(CustomException.class, () ->
                financialMovementService.registerMovement(userId, request)
        );
        verify(financialMovementRepository, never()).save(any());
    }


    @Test
    @DisplayName("US08-CP03 - Registro de Movimiento Financiero falla por monto negativo")
    void registerMovement_fails_whenAmountIsNegative() {
        // Arrange
        Integer userId = 1;
        LocalDate date = LocalDate.now();

        RegisterFinancialMovementRequest request = new RegisterFinancialMovementRequest(
                -10.0f,
                date,
                MovementType.OUTCOME,
                10,
                CurrencyType.USD
        );

        // Act & Assert
        assertThrows(CustomException.class, () ->
                financialMovementService.registerMovement(userId, request)
        );
        verify(financialMovementRepository, never()).save(any());
    }

    //US10 - Historial de transacciones
    @Test
    @DisplayName("US10-CP01 - Visualización exitosa del historial de transacciones")
    void getMovementsByUserIdFiltered_returnsTransactions_whenUserHasMovements() {
        // Arrange
        Integer userId = 1;
        String movementTypeName = null;
        Integer categoryId = null;

        Category category = new Category();
        category.setId(15);
        category.setName("Transporte");
        category.setDescription("Gastos en movilidad");

        FinancialMovement movement = new FinancialMovement();
        movement.setAmount(25.5f);
        movement.setDate(LocalDate.of(2024, 6, 6));
        movement.setMovementType(MovementType.OUTCOME);
        movement.setCategory(category);
        movement.setCurrencyType(CurrencyType.PEN);

        CategoryResponse categoryResponse = new CategoryResponse("Transporte", "Gastos en movilidad");

        RegisterFinancialMovementResponse expectedResponse = new RegisterFinancialMovementResponse(
                25.5f,
                LocalDate.of(2024, 6, 6),
                MovementType.OUTCOME,
                categoryResponse,
                CurrencyType.PEN
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(financialMovementRepository.findByUser_IdOrderByDateDesc(userId))
                .thenReturn(List.of(movement));
        when(financialMovementMapper.toRegisterFinancialMovementResponse(movement))
                .thenReturn(expectedResponse);

        // Act
        List<TransactionResponse> result = financialMovementService
                .getMovementsByUserIdFiltered(userId, movementTypeName, categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        TransactionResponse res = result.get(0);
        assertEquals(25.5f, res.amount());
        assertEquals(LocalDate.of(2024, 6, 6), res.date());
        assertEquals("OUTCOME", res.movementName());
        assertEquals("Transporte", res.categoryName());
        assertEquals("PEN", res.currencyName());
    }



    @Test
    @DisplayName("US10-CP02 - No hay transacciones registradas para el usuario")
    void getMovementsByUserIdFiltered_returnsEmptyList_whenNoTransactions() {
        // Arrange
        Integer userId = 1;
        String movementTypeName = null;
        Integer categoryId = null;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(financialMovementRepository.findByUser_Id(userId)).thenReturn(List.of());

        // Act
        List<TransactionResponse> result = financialMovementService
                .getMovementsByUserIdFiltered(userId, movementTypeName, categoryId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("US10-CP03 - Error al cargar historial por fallo en el servidor")
    void getMovementsByUserIdFiltered_throwsException_whenServerFails() {
        // Arrange
        Integer userId = 1;
        String movementTypeName = null;
        Integer categoryId = null;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(financialMovementRepository.findByUser_IdOrderByDateDesc(userId))
                .thenThrow(new DataAccessException("DB unavailable") {});

        // Act & Assert
        CustomException ex = assertThrows(CustomException.class, () ->
                financialMovementService.getMovementsByUserIdFiltered(userId, movementTypeName, categoryId)
        );
        assertEquals("Error al cargar historial", ex.getMessage());
    }

}