package com.example.financelloapi.model.entity;

import com.example.financelloapi.model.enums.CurrencyType;
import com.example.financelloapi.model.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "financial_movements")
public class FinancialMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Float amount;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currencyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}