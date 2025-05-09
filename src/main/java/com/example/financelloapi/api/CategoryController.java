package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.dto.response.CategoryResponse;
import com.example.financelloapi.dto.response.CategorySimpleResponse;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestParam Integer userId, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping
    public List<CategorySimpleResponse> getCategoryNamesByUserId(@RequestParam Integer userId) {
        return categoryService.getCategoryNamesByUserId(userId);
    }
}
