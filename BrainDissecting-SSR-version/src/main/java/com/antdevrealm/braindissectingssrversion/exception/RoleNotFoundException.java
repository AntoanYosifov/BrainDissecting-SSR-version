package com.antdevrealm.braindissectingssrversion.exception;

import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(UserRole role) {
        super("Role: " + role.toString() + " does not exist!");
    }
}
