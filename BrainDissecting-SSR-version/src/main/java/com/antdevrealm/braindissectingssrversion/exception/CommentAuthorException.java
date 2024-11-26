package com.antdevrealm.braindissectingssrversion.exception;

public class CommentAuthorException extends RuntimeException {
    public CommentAuthorException(long userId, long commentId) {
        super("User with ID: " + userId + " is not the author of comment with ID: " + commentId);
    }
}
