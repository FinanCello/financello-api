package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdCustom(@Param("id") Integer id);
    Optional<User> findByEmail(String email);
}
