package com.example.financelloapi.repository;
import com.example.financelloapi.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByNameAndUserId(String name, Integer userId);
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findByUser_Id(Integer userId);
}
