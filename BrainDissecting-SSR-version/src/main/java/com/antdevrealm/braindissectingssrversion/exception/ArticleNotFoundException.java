package com.antdevrealm.braindissectingssrversion.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(long articleId) {
        super("Article with ID: " + articleId + " not found!");
    }
}
