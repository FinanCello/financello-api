package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.SpendingLimitRequest;
import com.example.financelloapi.dto.test.SpendingLimitAlertResponse;
import com.example.financelloapi.dto.test.SpendingLimitResponse;
import com.example.financelloapi.exception.DuplicateLimitException;
import com.example.financelloapi.exception.InvalidLimitAmountException;
import com.example.financelloapi.mapper.SpendingLimitMapper;
import com.example.financelloapi.model.entity.SpendingLimit;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.SpendingLimitRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.AuthService;
import com.example.financelloapi.service.SpendingLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


public class SpendingLimitServiceUnitTest {

    @Mock private SpendingLimitRepository spendingLimitRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AuthService authService;
    @Mock private SpendingLimitMapper spendingLimitMapper;
    @Mock private UserRepository userRepository;
    @Mock private FinancialMovementRepository financialMovementRepository;
    @InjectMocks private SpendingLimitService spendingLimitService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    //US07: Definir limite de gasto
    @Test
    @DisplayName("US07-CP01 - Registrar limite valido")
    void createSpendingLimit_valid_returnResponse() {
        Integer userId = 1;
        Integer categoryId = 2;
        Float monthlyLimit = 150f;

        SpendingLimitRequest request = new SpendingLimitRequest(categoryId, monthlyLimit, userId);
        User user = new User();
        user.setId(userId);
        Category category = new Category();
        category.setId(categoryId);
        SpendingLimit entity = new SpendingLimit();
        entity.setUser(user);
        entity.setCategory(category);
        entity.setMonthlyLimit(monthlyLimit);
        SpendingLimitResponse expectedResponse = new SpendingLimitResponse("Transporte", monthlyLimit);

        when(spendingLimitRepository.findByCategory_IdAndUser_Id(categoryId, userId)).thenReturn(Optional.empty());
        when(userRepository.findByIdCustom(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(spendingLimitRepository.save(any(SpendingLimit.class))).thenReturn(entity);
        when(spendingLimitMapper.toResponse(entity)).thenReturn(expectedResponse);

        SpendingLimitResponse result = spendingLimitService.createSpendingLimit(userId, request);

        assertNotNull(result);
        assertEquals("Transporte", result.categoryName());
        assertEquals(monthlyLimit, result.monthlyLimit());

    }

    @Test
    @DisplayName("US07-CP02 - Limite ya existe")
    void createSpendingLimit_alreadyExists_throwsException() {
        Integer userId = 1;
        Integer categoryId = 2;
        Float monthlyLimit = 150f;

        SpendingLimitRequest request = new SpendingLimitRequest(categoryId, monthlyLimit, userId);

        SpendingLimit existingLimit = new SpendingLimit();
        when(spendingLimitRepository.findByCategory_IdAndUser_Id(categoryId, userId)).thenReturn(Optional.of(existingLimit));

        DuplicateLimitException exception = assertThrows(DuplicateLimitException.class, () -> {
            spendingLimitService.createSpendingLimit(userId, request);
        });

        assertEquals("Ya existe un limite para esta categoria", exception.getMessage());
    }

    @Test
    @DisplayName("US07-CP03 - Registrar límite con monto inválido")
    void createSpendingLimit_invalidAmount_throwsException() {
        Integer userId = 1;
        Integer categoryId = 2;
        Float invalidLimit = 0f;

        SpendingLimitRequest request = new SpendingLimitRequest(categoryId, invalidLimit, userId);

        InvalidLimitAmountException exception = assertThrows(
                InvalidLimitAmountException.class,
                () -> spendingLimitService.createSpendingLimit(userId, request)
        );

        assertEquals("El limite debe de ser mayor que 0", exception.getMessage());
    }



    //US09: Ver alerta por sobrepaso de limite
    @Test
    @DisplayName("US09-CP01 - Ver alerta cuando se supera el límite")
    void getAlerts_whenOverLimit_returnsAlert() {
        Integer userId = 1;
        Integer categoryId = 2;
        Float monthlyLimit = 200f;

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Alimentación");

        SpendingLimit limit = new SpendingLimit();
        limit.setUser(user);
        limit.setCategory(category);
        limit.setMonthlyLimit(monthlyLimit);

        FinancialMovement movement1 = new FinancialMovement();
        movement1.setAmount(120f);
        movement1.setMovementType(MovementType.OUTCOME);
        movement1.setCategory(category);
        movement1.setUser(user);
        movement1.setDate(LocalDate.now());

        FinancialMovement movement2 = new FinancialMovement();
        movement2.setAmount(100f);
        movement2.setMovementType(MovementType.OUTCOME);
        movement2.setCategory(category);
        movement2.setUser(user);
        movement2.setDate(LocalDate.now());

        when(spendingLimitRepository.findByUser_Id(userId)).thenReturn(List.of(limit));
        when(financialMovementRepository.findByUser_IdAndCategory_IdAndMovementTypeAndDateBetween(eq(userId), eq(categoryId), eq(MovementType.OUTCOME), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(movement1, movement2));

        List<SpendingLimitAlertResponse> alerts = spendingLimitService.getAlerts(userId);

        assertEquals(1, alerts.size());
        SpendingLimitAlertResponse alert = alerts.get(0);

        assertEquals("Alimentación", alert.categoryName());
        assertEquals(220f, alert.totalSpent());
        assertTrue(alert.overLimit());
        assertEquals("¡Has superado tu limite en la categoria Alimentación!", alert.alertMessage());
    }

    @Test
    @DisplayName("US09-CP02 - No supera limite, no hay alerta")
    void getAlerts_UnderLimit_returnsNoAlert() {
        Integer userId = 1;
        Integer categoryId = 2;
        Float monthlyLimit = 300f;

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Educación");

        SpendingLimit limit = new SpendingLimit();
        limit.setUser(user);
        limit.setCategory(category);
        limit.setMonthlyLimit(monthlyLimit);

        FinancialMovement movement = new FinancialMovement();
        movement.setAmount(100f);
        movement.setMovementType(MovementType.OUTCOME);
        movement.setCategory(category);
        movement.setUser(user);
        movement.setDate(LocalDate.now());

        when(spendingLimitRepository.findByUser_Id(userId)).thenReturn(List.of(limit));
        when(financialMovementRepository.findByUser_IdAndCategory_IdAndMovementTypeAndDateBetween(eq(userId), eq(categoryId), eq(MovementType.OUTCOME), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(movement));

        List<SpendingLimitAlertResponse> alerts = spendingLimitService.getAlerts(userId);

        assertEquals(0, alerts.size());
    }

    @Test
    @DisplayName("US09-CP03 - Sin movimientos no hay alerta")
    void getAlerts_NoMovements_returnsNoAlert() {
        Integer userId = 1;
        Integer categoryId = 2;
        Float monthlyLimit = 100f;

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Salud");

        SpendingLimit limit = new SpendingLimit();
        limit.setUser(user);
        limit.setCategory(category);
        limit.setMonthlyLimit(monthlyLimit);

        when(spendingLimitRepository.findByUser_Id(userId)).thenReturn(List.of(limit));
        when(financialMovementRepository.findByUser_IdAndCategory_IdAndMovementTypeAndDateBetween(
                eq(userId), eq(categoryId), eq(MovementType.OUTCOME), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of());

        List<SpendingLimitAlertResponse> alerts = spendingLimitService.getAlerts(userId);

        assertEquals(0, alerts.size());
    }

    @Test
    @DisplayName("US09-CP04 - Multiples alertas")
    void getAlerts_MultipleLimitsExceeded_returnsMultipleAlerts() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        // Categoría 1
        Category cat1 = new Category();
        cat1.setId(10);
        cat1.setName("Ocio");

        SpendingLimit limit1 = new SpendingLimit();
        limit1.setUser(user);
        limit1.setCategory(cat1);
        limit1.setMonthlyLimit(100f);

        FinancialMovement m1 = new FinancialMovement();
        m1.setUser(user);
        m1.setCategory(cat1);
        m1.setAmount(150f);
        m1.setMovementType(MovementType.OUTCOME);
        m1.setDate(LocalDate.now());

        // Categoría 2
        Category cat2 = new Category();
        cat2.setId(20);
        cat2.setName("Comida");

        SpendingLimit limit2 = new SpendingLimit();
        limit2.setUser(user);
        limit2.setCategory(cat2);
        limit2.setMonthlyLimit(50f);

        FinancialMovement m2 = new FinancialMovement();
        m2.setUser(user);
        m2.setCategory(cat2);
        m2.setAmount(60f);
        m2.setMovementType(MovementType.OUTCOME);
        m2.setDate(LocalDate.now());

        when(spendingLimitRepository.findByUser_Id(userId)).thenReturn(List.of(limit1, limit2));
        when(financialMovementRepository.findByUser_IdAndCategory_IdAndMovementTypeAndDateBetween(eq(userId), eq(10), eq(MovementType.OUTCOME), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(m1));
        when(financialMovementRepository.findByUser_IdAndCategory_IdAndMovementTypeAndDateBetween(eq(userId), eq(20), eq(MovementType.OUTCOME), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(List.of(m2));

        List<SpendingLimitAlertResponse> alerts = spendingLimitService.getAlerts(userId);

        assertEquals(2, alerts.size());
    }

}
