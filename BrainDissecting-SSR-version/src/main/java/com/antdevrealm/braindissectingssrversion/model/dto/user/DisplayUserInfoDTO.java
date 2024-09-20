package com.antdevrealm.braindissectingssrversion.model.dto.user;

import java.util.ArrayList;
import java.util.List;

public class DisplayUserInfoDTO {
    private long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private List<String> roles;

    public DisplayUserInfoDTO() {
        roles = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public DisplayUserInfoDTO setId(long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DisplayUserInfoDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public DisplayUserInfoDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public DisplayUserInfoDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public DisplayUserInfoDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public List<String> getRoles() {
        return roles;
    }

    public DisplayUserInfoDTO setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }
}
