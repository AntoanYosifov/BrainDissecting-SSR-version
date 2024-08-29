package com.antdevrealm.braindissectingssrversion.model.entity;

import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "roles")
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, unique = true)
    private UserRole role;

    public long getId() {
        return id;
    }

    public UserRoleEntity setId(long id) {
        this.id = id;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public UserRoleEntity setRole(UserRole role) {
        this.role = role;
        return this;
    }
}
