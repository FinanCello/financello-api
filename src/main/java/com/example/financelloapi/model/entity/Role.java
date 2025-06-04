package com.example.financelloapi.model.entity;

import com.example.financelloapi.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    // Constructor
    public Role() {}

    public Role(Integer id, RoleType roleType) {
        this.id = id;
        this.roleType = roleType;
    }

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
}