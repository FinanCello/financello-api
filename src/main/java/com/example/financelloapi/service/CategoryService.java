package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.response.CategoryTotalResponse;
import com.example.financelloapi.dto.response.RecentMovementResponse;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.exception.*;
import com.example.financelloapi.dto.test.CategorySimpleResponse;
import com.example.financelloapi.mapper.CategoryMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;
    private final FinancialMovementRepository financialMovementRepository;

    @Transactional
    public CategoryResponse createCategory(Integer userId, CategoryRequest request) {
        if (categoryRepository.existsByNameAndUserId(request.name(), userId)) {
            throw new DuplicateResourceException("La categoría ya existe");
        }

        User user = userRepository.findByIdCustom(userId)
                .orElseThrow(() -> new UserDoesntExistException("Usuario no encontrado"));

        Category newCategory = categoryMapper.toCategoryEntity(request, user);
        Category savedCategory = categoryRepository.save(newCategory);

        CategoryResponse response = categoryMapper.toCategoryResponse(savedCategory);
        return response;

    }

    @Transactional
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("La categoría no existe");
        }

        if (financialMovementRepository.existsByCategory_Id(id)) {
            throw new IllegalStateException("La categoría no puede eliminarse porque está asociada a movimientos financieros.");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public CategoryResponse updateCategory(Integer categoryId, CategoryRequest request) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("La categoría no existe");
        }

        Category category = categoryRepository.findByCategoryId(categoryId).orElseThrow();

        Integer userId = category.getUser().getId();

        // valida si el nombre cambia primero
        if (!category.getName().equals(request.name())) {
            if (categoryRepository.existsByNameAndUser_IdAndIdNot(request.name(), userId, categoryId)) {
                throw new DuplicateResourceException("Ya existe otra categoría con ese nombre");
            }
            category.setName(request.name());
        }
        category.setDescription(request.description());

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }


    public CategorySimpleResponse getCategory(Category category) {
        return new CategorySimpleResponse(category.getId(), category.getName());
    }

    public List<CategorySimpleResponse> getCategoryNamesByUserId(Integer userId) {
        userRepository.findByIdCustom(userId)
                .orElseThrow(() -> new UserDoesntExistException("Usuario no encontrado"));

        List<Category> categories = categoryRepository.findByUser_Id(userId);

        return categories.stream()
                .map(this::getCategory)
                .toList();
    }

    // Nuevos métodos para el frontend
    public CategoryTotalResponse getTotalExpensesByCategory(Integer userId, Integer categoryId) {
        // Verificar que la categoría existe y pertenece al usuario
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("La categoría no pertenece al usuario especificado");
        }

        Float totalExpenses = financialMovementRepository.sumAmountByCategoryAndMovementType(
                categoryId, MovementType.OUTCOME, userId);
        
        return new CategoryTotalResponse(
                categoryId,
                category.getName(),
                totalExpenses != null ? totalExpenses : 0.0f,
                "OUTCOME"
        );
    }

    public CategoryTotalResponse getTotalIncomesByCategory(Integer userId, Integer categoryId) {
        // Verificar que la categoría existe y pertenece al usuario
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("La categoría no pertenece al usuario especificado");
        }

        Float totalIncomes = financialMovementRepository.sumAmountByCategoryAndMovementType(
                categoryId, MovementType.INCOME, userId);
        
        return new CategoryTotalResponse(
                categoryId,
                category.getName(),
                totalIncomes != null ? totalIncomes : 0.0f,
                "INCOME"
        );
    }

    public List<RecentMovementResponse> getRecentMovementsByCategory(Integer userId, Integer categoryId, Integer limit) {
        // Verificar que la categoría existe y pertenece al usuario
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("La categoría no pertenece al usuario especificado");
        }

        Pageable pageable = PageRequest.of(0, limit != null ? limit : 10);
        List<FinancialMovement> movements = financialMovementRepository
                .findByCategory_IdAndCategory_User_IdOrderByDateDescIdDesc(categoryId, userId, pageable);

        return movements.stream()
                .map(movement -> new RecentMovementResponse(
                        movement.getId(),
                        movement.getAmount(),
                        movement.getDate(),
                        movement.getCategory().getName(),
                        movement.getCategory().getDescription(),
                        movement.getMovementType().toString()
                ))
                .toList();
    }
}