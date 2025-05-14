package com.example.financelloapi.repository;

import com.example.financelloapi.model.entity.SpendingLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface SpendingLimitRepository extends JpaRepository<SpendingLimit, Integer> {
    Optional<SpendingLimit> findByCategory_IdAndUser_Id(Integer categoryId, Integer userId);

    List<SpendingLimit> findByUser_Id(Integer userId);
}
