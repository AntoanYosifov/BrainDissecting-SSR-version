package com.antdevrealm.braindissectingssrversion.service;

public interface ModeratorService {

    boolean approveArticle(Long articleId);
    boolean rejectArticle(Long articleId);
}
