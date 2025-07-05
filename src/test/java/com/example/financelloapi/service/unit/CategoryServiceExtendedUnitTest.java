package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.response.CategoryTotalResponse;
import com.example.financelloapi.dto.response.RecentMovementResponse;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.entity.Role;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.enums.CurrencyType;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.model.enums.RoleType;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceExtendedUnitTest {

    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private FinancialMovementRepository financialMovementRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    public void setUp() {
        Role role = new Role(1, RoleType.BASIC);
        
        testUser = User.builder()
                .id(1)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .password("password123")
                .role(role)
                .build();

        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Alimentación");
        testCategory.setDescription("Gastos en comida");
        testCategory.setUser(testUser);
    }

    @Test
    @DisplayName("Obtener total de gastos por categoría - Debe retornar CategoryTotalResponse")
    void getTotalExpensesByCategory_validRequest_returnsCategoryTotalResponse() {
        // Arrange
        Float totalExpenses = 1500.0f;
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(financialMovementRepository.sumAmountByCategoryAndMovementType(1, MovementType.OUTCOME, 1))
                .thenReturn(totalExpenses);

        // Act
        CategoryTotalResponse result = categoryService.getTotalExpensesByCategory(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.categoryId());
        assertEquals("Alimentación", result.categoryName());
        assertEquals(1500.0f, result.totalAmount());
        assertEquals("OUTCOME", result.movementType());

        verify(categoryRepository, times(1)).findById(1);
        verify(financialMovementRepository, times(1))
                .sumAmountByCategoryAndMovementType(1, MovementType.OUTCOME, 1);
    }

    @Test
    @DisplayName("Obtener total de ingresos por categoría - Debe retornar CategoryTotalResponse")
    void getTotalIncomesByCategory_validRequest_returnsCategoryTotalResponse() {
        // Arrange
        Float totalIncomes = 3000.0f;
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(financialMovementRepository.sumAmountByCategoryAndMovementType(1, MovementType.INCOME, 1))
                .thenReturn(totalIncomes);

        // Act
        CategoryTotalResponse result = categoryService.getTotalIncomesByCategory(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.categoryId());
        assertEquals("Alimentación", result.categoryName());
        assertEquals(3000.0f, result.totalAmount());
        assertEquals("INCOME", result.movementType());

        verify(categoryRepository, times(1)).findById(1);
        verify(financialMovementRepository, times(1))
                .sumAmountByCategoryAndMovementType(1, MovementType.INCOME, 1);
    }

    @Test
    @DisplayName("Obtener movimientos recientes por categoría - Debe retornar lista de RecentMovementResponse")
    void getRecentMovementsByCategory_validRequest_returnsRecentMovements() {
        // Arrange
        FinancialMovement movement1 = new FinancialMovement();
        movement1.setId(1);
        movement1.setAmount(250.0f);
        movement1.setDate(LocalDate.now().minusDays(1));
        movement1.setMovementType(MovementType.OUTCOME);
        movement1.setCategory(testCategory);
        movement1.setCurrencyType(CurrencyType.USD);

        FinancialMovement movement2 = new FinancialMovement();
        movement2.setId(2);
        movement2.setAmount(500.0f);
        movement2.setDate(LocalDate.now().minusDays(2));
        movement2.setMovementType(MovementType.INCOME);
        movement2.setCategory(testCategory);
        movement2.setCurrencyType(CurrencyType.USD);

        List<FinancialMovement> movements = Arrays.asList(movement1, movement2);

        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(financialMovementRepository.findByCategory_IdAndCategory_User_IdOrderByDateDescIdDesc(
                eq(1), eq(1), any(Pageable.class))).thenReturn(movements);

        // Act
        List<RecentMovementResponse> result = categoryService.getRecentMovementsByCategory(1, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        RecentMovementResponse first = result.get(0);
        assertEquals(1, first.movementId());
        assertEquals(250.0f, first.amount());
        assertEquals("Alimentación", first.categoryName());
        assertEquals("Gastos en comida", first.categoryDescription());
        assertEquals("OUTCOME", first.movementType());

        RecentMovementResponse second = result.get(1);
        assertEquals(2, second.movementId());
        assertEquals(500.0f, second.amount());
        assertEquals("INCOME", second.movementType());

        verify(categoryRepository, times(1)).findById(1);
        verify(financialMovementRepository, times(1))
                .findByCategory_IdAndCategory_User_IdOrderByDateDescIdDesc(eq(1), eq(1), any(Pageable.class));
    }

    @Test
    @DisplayName("Obtener total con categoría inexistente - Debe lanzar excepción")
    void getTotalExpensesByCategory_categoryNotFound_throwsException() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> categoryService.getTotalExpensesByCategory(1, 1));
        
        verify(categoryRepository, times(1)).findById(1);
        verify(financialMovementRepository, never()).sumAmountByCategoryAndMovementType(any(), any(), any());
    }

    @Test
    @DisplayName("Obtener total con usuario incorrecto - Debe lanzar excepción")
    void getTotalExpensesByCategory_wrongUser_throwsException() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> categoryService.getTotalExpensesByCategory(2, 1)); // Usuario 2 en lugar de 1
        
        verify(categoryRepository, times(1)).findById(1);
        verify(financialMovementRepository, never()).sumAmountByCategoryAndMovementType(any(), any(), any());
    }
}
