package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.antdevrealm.braindissectingssrversion.model.enums.Status.APPROVED;
import static com.antdevrealm.braindissectingssrversion.model.enums.Status.PENDING;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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



}
