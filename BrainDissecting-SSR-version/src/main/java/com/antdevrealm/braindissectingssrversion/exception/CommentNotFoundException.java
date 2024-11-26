package com.antdevrealm.braindissectingssrversion.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(long commentId) {
        super("Comment with ID: " + commentId + " not found!");
    }
}
