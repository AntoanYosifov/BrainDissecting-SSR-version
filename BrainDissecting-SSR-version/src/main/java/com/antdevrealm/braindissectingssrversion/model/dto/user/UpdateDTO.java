package com.antdevrealm.braindissectingssrversion.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateDTO {
    @NotNull(message = "{userEntity.notnull.username}")
    @Size(min = 5, max = 20, message = "{userEntity.username.length}")
    private String newUsername;

    @NotNull(message = "{userEntity.notnull.username}")
    @Size(min = 5, max = 20, message = "{userEntity.username.length}")
    private String confirmUsername;

//    @NotNull(message = "{userEntity.registration.notBlank.email}")
//    @NotBlank(message = "{userEntity.registration.notBlank.email}")
    @Email(message = "{userEntity.registration.email}")
    private String newEmail;

    public UpdateDTO() {}

    public String getNewUsername() {
        return newUsername;
    }

    public UpdateDTO setNewUsername(String newUsername) {
        this.newUsername = newUsername;
        return this;
    }

    public String getConfirmUsername() {
        return confirmUsername;
    }

    public UpdateDTO setConfirmUsername(String confirmUsername) {
        this.confirmUsername = confirmUsername;
        return this;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public UpdateDTO setNewEmail(String newEmail) {
        this.newEmail = newEmail;
        return this;
    }
}
