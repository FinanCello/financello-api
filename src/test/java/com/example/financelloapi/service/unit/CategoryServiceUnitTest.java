package com.example.financelloapi.service.unit;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.dto.test.CategorySimpleResponse;
import com.example.financelloapi.exception.DuplicateResourceException;
import com.example.financelloapi.exception.ResourceNotFoundException;
import com.example.financelloapi.exception.UserDoesntExistException;
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

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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

    //POST
    @Test
    @DisplayName("CP01 - Crear categoría valida")
    void createCategory_valid_returnCreated() {
        Integer userId = 1;
        CategoryRequest request = new CategoryRequest("Ocio", "Gastos en actividades de ocio");
        Category entity = new Category();
        User user = new User();
        user.setId(userId);
        CategoryResponse response = new CategoryResponse("Ocio", "Gastos en actividades de ocio");

        when(categoryRepository.existsByNameAndUserId(request.name(), user.getId())).thenReturn(false);
        when(userRepository.findByIdCustom(user.getId())).thenReturn(Optional.of(user));
        when(categoryMapper.toCategoryEntity(request, user)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.toCategoryResponse(entity)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(user.getId(), request);

        assertEquals("Ocio", result.name());
        assertNotNull(result);
    }

    @Test
    @DisplayName("CP02 - Crear categoría con nombre duplicado")
    void createCategory_duplicatedCategory_throwException() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        CategoryRequest request = new CategoryRequest("Ocio", "Gastos en actividades de ocio");

        when(categoryRepository.existsByNameAndUserId(request.name(), user.getId())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> categoryService.createCategory(user.getId(), request));
    }

    @Test
    @DisplayName("CP03 - Crear categoría cuando el usuario no existe")
    void createCategory_userNotFound_throwException() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        CategoryRequest request = new CategoryRequest("Ocio", "Gastos en actividades de ocio");

        when(userRepository.findByIdCustom(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserDoesntExistException.class,
                () -> categoryService.createCategory(user.getId(), request));
    }

    //DELETE
    @Test
    @DisplayName("CP04 - Eliminar categoría existente")
    void deleteCategory_valid_executesDelete() {
        Integer categoryId = 1;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        when(financialMovementRepository.existsByCategory_Id(category.getId())).thenReturn(false);

        categoryService.deleteCategory(category.getId());

        verify(categoryRepository).deleteById(category.getId());
    }

    @Test
    @DisplayName("CP05 - Eliminar categoría inexistente")
    void deleteCategory_notFound_throwException() {
        Integer categoryId = 1;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.existsById(category.getId())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId));
    }

    @Test
    @DisplayName("CP06 - Eliminar categoría asociada a movimientos financieros")
    void deleteCategory_associated_throwException() {
        Integer categoryId = 1;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        when(financialMovementRepository.existsByCategory_Id(category.getId())).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> categoryService.deleteCategory(categoryId));
    }

    //UPDATE
    @Test
    @DisplayName("CP07 - Editar categoría existente")
    void updateCategory_valid_returnUpdated() {
        Integer categoryId = 1;
        CategoryRequest request = new CategoryRequest("Ocio", "Gastos en actividades de ocio");

        User user = new User();
        user.setId(42);

        Category entity = new Category();
        entity.setId(categoryId);
        entity.setName("Mascotas");
        entity.setUser(user);
        entity.setDescription("Gastos en mascotas");

        Category updatedEntity = new Category();
        updatedEntity.setId(categoryId);
        updatedEntity.setName("Ocio");
        updatedEntity.setDescription("Gastos en actividades de ocio");
        updatedEntity.setUser(user);

        CategoryResponse response = new CategoryResponse("Ocio", "Gastos en actividades de ocio");

        when(categoryRepository.existsById(entity.getId())).thenReturn(true);
        when(categoryRepository.findByCategoryId(entity.getId())).thenReturn(Optional.of(entity));
        when(categoryRepository.existsByNameAndUser_IdAndIdNot("Ocio", user.getId(), entity.getId())).thenReturn(false);
        when(categoryRepository.save(entity)).thenReturn(updatedEntity);
        when(categoryMapper.toCategoryResponse(updatedEntity)).thenReturn(response);

        CategoryResponse result = categoryService.updateCategory(entity.getId(), request);

        assertEquals("Ocio", result.name());
    }

    @Test
    @DisplayName("CP08 - Editar categoría inexistente")
    void updateCategory_notFound_returnUpdated() {
        Integer categoryId = 1;
        CategoryRequest request = new CategoryRequest("Ocio", "Gastos en actividades de ocio");

        User user = new User();
        user.setId(42);

        Category entity = new Category();
        entity.setId(categoryId);
        entity.setName("Mascotas");
        entity.setUser(user);
        entity.setDescription("Gastos en mascotas");

        when(categoryRepository.existsById(entity.getId())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, request));
    }

    @Test
    @DisplayName("CP09 - Editar categoría repetida")
    void updateCategory_duplicatedCategory_throwException() {
        Integer categoryId = 1;
        CategoryRequest request = new CategoryRequest("Ocio", "Gastos en actividades de ocio");
        User user = new User();
        user.setId(42);

        Category entity = new Category();
        entity.setId(categoryId);
        entity.setName("Mascotas");
        entity.setUser(user);
        entity.setDescription("Gastos en mascotas");

        when(categoryRepository.existsById(entity.getId())).thenReturn(true);
        when(categoryRepository.findByCategoryId(entity.getId())).thenReturn(Optional.of(entity));
        when(categoryRepository.existsByNameAndUser_IdAndIdNot("Ocio", user.getId(), entity.getId())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> categoryService.updateCategory(entity.getId(), request));
    }

    //GET
    @Test
    @DisplayName("CP10 - Obtener lista de categorías")
    void listCategories_valid_returnList() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        Category cat1 = new Category();
        cat1.setName("Food");
        Category cat2 = new Category();
        cat2.setName("Travel");

        when(userRepository.findByIdCustom(user.getId())).thenReturn(Optional.of(user));
        when(categoryRepository.findByUser_Id(user.getId())).thenReturn(List.of(cat1, cat2));

        List<CategorySimpleResponse> result = categoryService.getCategoryNamesByUserId(user.getId());

        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).name());
        assertEquals("Travel", result.get(1).name());

    }

    @Test
    @DisplayName("CP11 - Obtener lista de categorías con usuario inexistente")
    void listCategories_userNotFound_returnList() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        when(userRepository.findByIdCustom(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserDoesntExistException.class,
                () -> categoryService.getCategoryNamesByUserId(user.getId()));

    }
}
