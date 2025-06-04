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

    //US07
    @Test
    @DisplayName("CP01 - Registrar limite valido")
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
    @DisplayName("CP02 - No registrar límite si ya existe uno para la categoría y usuario")
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
    @DisplayName("CP03 - Registrar límite con monto inválido")
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



    //US09
    @Test
    @DisplayName("CP09 - Ver alerta cuando se supera el límite")
    void getAlerts_whenOverLimit_returnsAlert() {
        Integer userId = 1;
        Integer categoryId = 2;
        Float monthlyLimit = 200f;

        // Datos simulados
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
        assertEquals(220f, alert.totalSpent()); // 120 + 100
        assertTrue(alert.overLimit());
        assertEquals("¡Has superado tu limite en la categoriaAlimentación!", alert.alertMessage());
    }


}
