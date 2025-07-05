package com.example.financelloapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user; //asumo que es la edentidad de user que ya tenemos

    @ManyToOne
    private Achievement achievement;
    private LocalDateTime unlockedAt;

    // Getters/Setters
}