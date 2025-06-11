package com.example.financelloapi.api;

import com.example.financelloapi.dto.request.CategoryRequest;
import com.example.financelloapi.dto.test.CategoryResponse;
import com.example.financelloapi.dto.test.CategorySimpleResponse;
import com.example.financelloapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
@PreAuthorize("hasAnyRole('BASIC')")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestParam Integer userId, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable(name="id") Integer categoryId, @Valid @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(categoryService.updateCategory(categoryId, request), HttpStatus.OK);
    }

    @GetMapping("/{userid}")
    public ResponseEntity<List<CategorySimpleResponse>> getCategoryNamesByUserId(@PathVariable(name="userid") Integer userId) {
        return ResponseEntity.ok(categoryService.getCategoryNamesByUserId(userId));
    }

}
