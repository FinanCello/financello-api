package com.example.financelloapi.service;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.mapper.CategoryMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public Category createCategory(CategoryRequest request) {
        //if (categoryRepository.existsByName(request.name())){
        //    String message = "Esta categoría ya existe";
        //    throw new IllegalArgumentException(message);
        //}

        Category newCategory = categoryMapper.toCategoryEntity(request);
        return categoryRepository.save(newCategory);

    }

}
