package com.example.financelloapi.model.entity;

import com.example.financelloapi.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    // Roles básicos
    public static final Role ADMIN = new Role(1, RoleType.ADMIN);
    public static final Role BASIC = new Role(2, RoleType.BASIC);

    // Constructor, Getters, Setters
    public Role() {}

    public Role(Integer id, RoleType roleType) {
        this.id = id;
        this.roleType = roleType;
    }
}