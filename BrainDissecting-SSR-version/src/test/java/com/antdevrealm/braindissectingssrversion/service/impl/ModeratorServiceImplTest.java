package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.antdevrealm.braindissectingssrversion.model.enums.Status.APPROVED;
import static com.antdevrealm.braindissectingssrversion.model.enums.Status.PENDING;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModeratorServiceImplTest {

    private final long ARTICLE_ID = 1L;

    @Mock
    private ArticleRepository mockArticleRepository;

    @Captor
    private ArgumentCaptor<ArticleEntity> articleCaptor;

    private ModeratorServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new ModeratorServiceImpl(mockArticleRepository);
    }

    @Test
    void approveArticle_ShouldReturnTrueWhenArticleExistsAndStatusIsPending() {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(ARTICLE_ID);
        articleEntity.setStatus(PENDING);

        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));

        boolean result = toTest.approveArticle(ARTICLE_ID);

        verify(mockArticleRepository).save(articleCaptor.capture());
        ArticleEntity savedArticle = articleCaptor.getValue();

        Assertions.assertTrue(result);
        Assertions.assertEquals(ARTICLE_ID, savedArticle.getId());
        Assertions.assertEquals(APPROVED, savedArticle.getStatus());
    }



    @Test
    void approveArticle_ShouldReturnFalseWhenArticleDoesNotExist() {
        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        boolean result = toTest.approveArticle(ARTICLE_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void approveArticle_ShouldReturnFalseWhenArticleStatusIsNotPending() {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(ARTICLE_ID);
        articleEntity.setStatus(APPROVED);

        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));

        boolean result = toTest.approveArticle(ARTICLE_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void approveAllArticles_ShouldReturnTrueWhenPendingArticlesExists() {
        ArticleEntity articleEntity1 = new ArticleEntity();
        articleEntity1.setId(ARTICLE_ID);
        articleEntity1.setStatus(PENDING);

        ArticleEntity articleEntity2 = new ArticleEntity();
        articleEntity1.setId(2L);
        articleEntity1.setStatus(PENDING);

        List<ArticleEntity> pendingArticles = new ArrayList<>();
        pendingArticles.add(articleEntity1);
        pendingArticles.add(articleEntity2);

        when(mockArticleRepository.count()).thenReturn(2L);
        when(mockArticleRepository.findPendingArticles()).thenReturn(pendingArticles);

        boolean result = toTest.approveAllArticles();

        verify(mockArticleRepository, times(2)).save(articleCaptor.capture());

        List<ArticleEntity> approvedArticles = articleCaptor.getAllValues();

        Assertions.assertTrue(result);
        Assertions.assertFalse(approvedArticles.isEmpty());
        Assertions.assertEquals(APPROVED, approvedArticles.getFirst().getStatus());
        Assertions.assertEquals(APPROVED, approvedArticles.get(1).getStatus());
    }

    @Test
    void approveAllArticles_ShouldReturnFalseWhenArticleRepositoryIsEmpty() {
        when(mockArticleRepository.count()).thenReturn(0L);

        boolean result = toTest.approveAllArticles();

        Assertions.assertFalse(result);
    }

    @Test
    void approveAllArticles_ShouldReturnFalseWhenThereIsNoPendingArticles() {
        when(mockArticleRepository.count()).thenReturn(2L);
        when(mockArticleRepository.findPendingArticles()).thenReturn(new ArrayList<>());

        boolean result = toTest.approveAllArticles();

        Assertions.assertFalse(result);
    }

    @Test
    void rejectArticle_ShouldReturnTrueWhenArticleExistsAndStatusIsPending() {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(ARTICLE_ID);
        articleEntity.setStatus(PENDING);

        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));

        boolean result = toTest.rejectArticle(ARTICLE_ID);

        verify(mockArticleRepository).delete(articleCaptor.capture());
        ArticleEntity rejectedArticle = articleCaptor.getValue();

        Assertions.assertTrue(result);
        Assertions.assertEquals(ARTICLE_ID, rejectedArticle.getId());
    }

    @Test
    void rejectArticle_ShouldReturnFalseWhenArticleDoesNotExist() {
        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        boolean result = toTest.rejectArticle(ARTICLE_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void rejectArticle_ShouldReturnFalseWhenArticleStatusIsNotPending() {
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(ARTICLE_ID);
        articleEntity.setStatus(APPROVED);

        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));

        boolean result = toTest.rejectArticle(ARTICLE_ID);

        Assertions.assertFalse(result);
    }


}
