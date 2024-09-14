package com.antdevrealm.braindissectingssrversion.exception;

public class NewUsernameConfirmUsernameException extends RuntimeException {
    public NewUsernameConfirmUsernameException(String newUsername, String confirmUsername) {
        super("Username: " + newUsername + " & Confirm New Username: " + confirmUsername + " Do not match!");
    }
}
