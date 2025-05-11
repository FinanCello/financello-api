package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.exception.CategoryAlreadyExistsException;
import com.example.financelloapi.exception.CategoryNotFoundException;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.exception.CategoryInUseException;
import com.example.financelloapi.exception.CategoryNotFoundException;
import com.example.financelloapi.mapper.CategoryMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            throw new CategoryAlreadyExistsException("La categoría ya existe");
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
        if (!categoryRepository.existsById(id)){
            String message = "La categoría no existe";
            throw new CategoryNotFoundException(message);
        }

        if (financialMovementRepository.existsByCategory_Id(id)) {
            throw new CategoryInUseException("La categoría no puede eliminarse porque está asociada a movimientos financieros.");
        }
        categoryRepository.deleteById(id);
    }
    @Transactional
    public CategoryResponse updateCategory(Integer categoryId, CategoryRequest request) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("La categoría no existe");
        }

        Category category = categoryRepository.findByCategoryId(categoryId).orElseThrow();

        Integer userId = category.getUser().getId();

        // valida si el nombre cambia primero
        if (!category.getName().equals(request.name())) {
            if (categoryRepository.existsByNameAndUser_IdAndIdNot(request.name(), userId, categoryId)) {
                throw new CategoryAlreadyExistsException("Ya existe otra categoría con ese nombre");
            }
            category.setName(request.name());
        }
        category.setDescription(request.description());

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

}
