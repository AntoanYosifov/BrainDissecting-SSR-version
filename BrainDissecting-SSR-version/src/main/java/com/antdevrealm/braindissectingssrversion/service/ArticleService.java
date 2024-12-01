package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;

import java.util.List;

public interface ArticleService {

    List<DisplayArticleDTO> getAllApproved();

    List<DisplayArticleDTO> getAllPending();

    List<DisplayArticleDTO> getAllByCategory(String categoryName);

    List<DisplayArticleDTO> getUserFavourites(Long userId);

    List<FetchArticleDTO> fetchArticles(String theme);

    void deleteArticle(Long articleId);

    boolean updateArticles(String theme);

    List<String> getThemes();

    boolean addTheme(String theme);

    boolean removeTheme(String theme);

    int countPendingArticles();
}
