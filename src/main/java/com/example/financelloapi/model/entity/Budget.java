package com.example.financelloapi.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "period", nullable = false, length = 50)
    private String period;

    @NotNull
    @Column(name = "total_income_planned", nullable = false)
    private Float totalIncomePlanned;

    @NotNull
    @Column(name = "total_outcome_planned", nullable = false)
    private Float totalOutcomePlanned;
}