package com.antdevrealm.braindissectingssrversion.exception;

public class PasswordConfirmPassMisMatchException extends RuntimeException{
    public PasswordConfirmPassMisMatchException() {
        super("Password and Confirm password do not match! Please try again.");
    }
}
