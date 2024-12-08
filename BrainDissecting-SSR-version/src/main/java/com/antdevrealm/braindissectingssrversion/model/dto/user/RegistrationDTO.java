package com.antdevrealm.braindissectingssrversion.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {

    @NotNull(message = "{userEntity.notnull.username}")
    @Size(min = 5, max = 20, message = "{userEntity.username.length}")
    private String username;

    @NotNull(message = "{userEntity.registration.notBlank.email}")
    @NotBlank(message = "{user.registration.notBlank.email}")
    @Email(message = "{userEntity.registration.email}")
    private String email;

    @NotNull(message = "{userEntity.notnull.password}")
    @Size(min = 3, max = 30, message = "{userEntity.registration.password.length}")
    private String password;

    @NotNull(message = "{userEntity.registration.notnull.password.confirm}")
    @Size(min = 3, max = 30, message = "{userEntity.registration.password.length}")
    private String confirmPassword;

    private String firstName;

    private String lastName;


    public RegistrationDTO() {
    }

    public String getUsername() {
        return username;
    }

    public RegistrationDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegistrationDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegistrationDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public RegistrationDTO setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RegistrationDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RegistrationDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
