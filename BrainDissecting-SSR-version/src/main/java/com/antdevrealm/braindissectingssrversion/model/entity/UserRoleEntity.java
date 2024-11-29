package com.antdevrealm.braindissectingssrversion.model.entity;

import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleEntity that = (UserRoleEntity) o;
        return role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }
}
