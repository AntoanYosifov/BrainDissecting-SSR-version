package com.antdevrealm.braindissectingssrversion.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {

    @NotNull(message = "{user.registration.notnull.username}")
    @Size(min = 5, max = 20, message = "{user.registration.username.length}")
    private String username;

    @NotNull(message = "{user.registration.notnull.email}")
    @NotBlank(message = "{user.registration.notBlank.email}")
    @Email(message = "{user.registration.email}")
    private String email;

    // TODO: After testing faze change min size to 8;
    @NotNull(message = "{user.registration.notnull.password}")
    @Size(min = 3, max = 30, message = "{user.registration.password}")
    private String password;

    @NotNull(message = "{user.registration.notnull.password.confirm}")

    @Size(min = 3, max = 30, message = "{user.registration.password}")
    private String confirmPassword;


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
}
