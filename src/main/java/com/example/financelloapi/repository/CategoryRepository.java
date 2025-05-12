package com.example.financelloapi.repository;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findById(Integer id);
    boolean existsByNameAndUserId(String name, Integer userId);
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId")
    Optional<Category> findByCategoryId(Integer categoryId);
    boolean existsByNameAndUser_IdAndIdNot(String name, Integer userId, Integer categoryId);
    boolean existsById(Integer categoryId);

    @Query("SELECT t FROM Category t WHERE t.user.id = :user_id")
    List<Category> findByUser_Id(@Param("user_id") Integer userId);

    Integer user(User user);
}
