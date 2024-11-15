package com.antdevrealm.braindissectingssrversion.model.entity;

import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToMany
    @JoinTable(name = "user_favourite",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "favourite_id"))
    private List<ArticleEntity> favourites;

    @OneToMany(targetEntity = CommentEntity.class, mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CommentEntity> comments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<UserRoleEntity> roles;

    public UserEntity() {
        roles = new ArrayList<>();
        comments = new ArrayList<>();
        favourites = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public UserEntity setUsername(String firstName) {
        this.username = firstName;
        return this;
    }


    public String getEmail() {
        return email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserEntity setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserEntity setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UserEntity setStatus(UserStatus userStatus) {
        this.status = userStatus;
        return this;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public UserEntity setComments(List<CommentEntity> comments) {
        this.comments = comments;
        return this;
    }

    public List<ArticleEntity> getFavourites() {
        return favourites;
    }

    public UserEntity setFavourites(List<ArticleEntity> favourites) {
        this.favourites = favourites;
        return this;
    }

    public List<UserRoleEntity> getRoles() {
        return roles;
    }

    public UserEntity setRoles(List<UserRoleEntity> roles) {
        this.roles = roles;
        return this;
    }

    public boolean isBanned() {
        return this.status.equals(UserStatus.BANNED);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + (password.isBlank()  ?  "N/A" : "USER/PASS") + '\'' +
                '}';
    }

}
