package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.mapper.CategoryMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CategoryServiceUnitTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;
    @Mock private UserRepository userRepository;
    @Mock private FinancialMovementRepository financialMovementRepository;
    @InjectMocks private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("CP01 - Crear categoría valida")
    void createCategory_valid_returnCreated() {
        Integer userId = 1;
        CategoryRequest request = new CategoryRequest("Ocio", "Gastos en actividades de ocio");
        Category entity = new Category();
        User user = new User();
        user.setId(userId);
        CategoryResponse response = new CategoryResponse("Ocio", "Gastos en actividades de ocio");

        when(categoryRepository.existsByNameAndUserId(request.name(), userId)).thenReturn(false);
        when(userRepository.findByIdCustom(userId)).thenReturn(Optional.of(user));
        when(categoryMapper.toCategoryEntity(request, user)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.toCategoryResponse(entity)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(userId, request);

        assertEquals("Ocio", result.name());
        assertNotNull(result);
    }
}
