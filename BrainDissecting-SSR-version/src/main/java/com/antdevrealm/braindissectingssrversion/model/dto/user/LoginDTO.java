package com.antdevrealm.braindissectingssrversion.model.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginDTO {

    @NotNull(message = "{userEntity.username.length}")
    @Size(min = 5, max = 20, message = "{userEntity.username.length}")
    private String username;

    @NotNull(message = "{userEntity.notnull.password}")
    @Size(min = 5, max = 30, message = "{userEntity.registration.password.length}")
    private String password;

    public LoginDTO() {}

    public String getUsername() {
        return username;
    }

    public LoginDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LoginDTO setPassword(String password) {
        this.password = password;
        return this;
    }
}
