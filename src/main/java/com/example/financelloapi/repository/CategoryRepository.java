package com.example.financelloapi.repository;
import com.example.financelloapi.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByNameAndUserId(String name, Integer userId);
}
