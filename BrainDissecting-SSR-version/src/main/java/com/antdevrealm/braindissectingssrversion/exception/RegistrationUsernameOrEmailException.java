package com.antdevrealm.braindissectingssrversion.exception;

public class RegistrationUsernameOrEmailException extends RuntimeException {

    public RegistrationUsernameOrEmailException(String username, String email) {
        super("Username: " + username + " or email: " + email + " already taken!");
    }
}
