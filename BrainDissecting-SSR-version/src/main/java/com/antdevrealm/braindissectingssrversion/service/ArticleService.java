package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;

import java.util.List;

public interface ArticleService {

    List<DisplayArticleDTO> getAllArticles();
}
