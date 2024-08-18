package com.antdevrealm.braindissectingssrversion.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {

    @NotNull
    @Size(min = 5, max = 20)
    private String username;

    @NotNull
    @Email
    private String email;

    // TODO: After testing faze change min size to 8;
    @NotNull
    @Size(min = 3, max = 30)
    private String password;

    @NotNull
    @Size(min = 3, max = 30)
    private String confirmPassword;


    public UserRegistrationDTO() {
    }

    public String getUsername() {
        return username;
    }

    public UserRegistrationDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRegistrationDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRegistrationDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public UserRegistrationDTO setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }
}
