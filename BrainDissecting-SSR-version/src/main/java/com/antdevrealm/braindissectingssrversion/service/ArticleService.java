package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.article.ArticleDTO;

import java.util.List;

public interface ArticleService {

    void updateArticles();

    List<ArticleDTO> getAllArticles();

    List<ArticleDTO> fetchArticles();
}
