package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.response.CategoryResponse;
import com.example.financelloapi.exception.CategoryInUseException;
import com.example.financelloapi.mapper.CategoryMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FinancialMovementRepository financialMovementRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())){
            String message = "La categoría ya existe";
            throw new IllegalArgumentException(message);
        }

        Category newCategory = categoryMapper.toCategoryEntity(request);
        Category savedCategory = categoryRepository.save(newCategory);

        CategoryResponse response = categoryMapper.toCategoryResponse(savedCategory);
        return response;

    }

    @Transactional
    public void deleteCategory(Integer id) {
        //if (!categoryRepository.existsById(id)){
        //    String message = "La categoría no existe";
        //    throw new IllegalArgumentException(message);
        //}

        if (financialMovementRepository.existsByCategory_Id(id)) {
            throw new CategoryInUseException("La categoría no puede eliminarse porque está asociada a movimientos financieros.");
        }
        categoryRepository.deleteById(id);
    }
}
