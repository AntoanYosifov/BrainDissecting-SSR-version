package com.antdevrealm.braindissectingssrversion.exception;

public class UsernameOrEmailException extends RuntimeException {

    public UsernameOrEmailException(String username, String email) {
        super("Username: " + username + " or email: " + email + " already taken!");
    }
}
