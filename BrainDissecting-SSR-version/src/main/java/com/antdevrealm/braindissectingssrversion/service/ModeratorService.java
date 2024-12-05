package com.antdevrealm.braindissectingssrversion.service;

public interface ModeratorService {

    boolean approveArticle(Long articleId);
    boolean approveAllArticles();
    boolean rejectArticle(Long articleId);
    boolean rejectAllArticles();

}
