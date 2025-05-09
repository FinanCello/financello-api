package com.example.financelloapi.repository;
import com.example.financelloapi.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByNameAndUserId(String name, Integer userId);
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId")
    Optional<Category> findByCategoryId(Integer categoryId);
    boolean existsByNameAndUser_IdAndIdNot(String name, Integer userId, Integer categoryId);
    boolean existsById(Integer categoryId);
}
