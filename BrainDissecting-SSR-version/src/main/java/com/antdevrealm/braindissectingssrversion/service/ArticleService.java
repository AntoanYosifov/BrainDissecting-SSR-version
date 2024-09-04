package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;

import java.util.List;

public interface ArticleService {

    void updateArticles();

    List<DisplayArticleDTO> getAllArticles();

    List<FetchArticleDTO> fetchArticles();
}
