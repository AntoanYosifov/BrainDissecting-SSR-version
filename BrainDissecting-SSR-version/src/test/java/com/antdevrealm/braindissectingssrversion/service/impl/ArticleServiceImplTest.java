package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private RestClient restClient;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;

    private ArticleServiceImpl articleService;

    @BeforeEach
    void setUp() {
        articleService = new ArticleServiceImpl(restClient, articleRepository,
                categoryService, modelMapper, userRepository);
    }

    @Test
    void getAllApproved_ReturnsMappedApprovedArticles() {
        ArticleEntity articleEntity1 = new ArticleEntity();
        articleEntity1.setId(1L);
        articleEntity1.setTitle("Article 1");
        articleEntity1.setStatus(Status.APPROVED);

        ArticleEntity articleEntity2 = new ArticleEntity();
        articleEntity2.setId(2L);
        articleEntity2.setTitle("Article 2");
        articleEntity2.setStatus(Status.APPROVED);

        List<ArticleEntity> approvedArticles = List.of(articleEntity1, articleEntity2);

        when(articleRepository.findApprovedArticles()).thenReturn(approvedArticles);

        DisplayArticleDTO dto1 = new DisplayArticleDTO();
        dto1.setId(1L);
        dto1.setTitle("Article 1");

        DisplayArticleDTO dto2 = new DisplayArticleDTO();
        dto2.setId(2L);
        dto2.setTitle("Article 2");

        when(modelMapper.map(articleEntity1, DisplayArticleDTO.class)).thenReturn(dto1);
        when(modelMapper.map(articleEntity2, DisplayArticleDTO.class)).thenReturn(dto2);

        List<DisplayArticleDTO> result = articleService.getAllApproved();

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    void getAllApproved_ReturnsEmptyListWhenNoApprovedArticlesExist() {
        when(articleRepository.findApprovedArticles()).thenReturn(List.of());

        List<DisplayArticleDTO> result = articleService.getAllApproved();

        assertEquals(0, result.size());
    }

    @Test
    void getAllPending_ReturnsEmptyListWhenNoPendingArticlesExist() {
        when(articleRepository.findPendingArticles()).thenReturn(List.of());

        List<DisplayArticleDTO> result = articleService.getAllPending();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllPending_ReturnsMappedApprovedArticles() {
        ArticleEntity articleEntity1 = new ArticleEntity();
        articleEntity1.setId(1L);
        articleEntity1.setTitle("Article 1");
        articleEntity1.setStatus(Status.PENDING);

        ArticleEntity articleEntity2 = new ArticleEntity();
        articleEntity2.setId(2L);
        articleEntity2.setTitle("Article 2");
        articleEntity2.setStatus(Status.PENDING);

        List<ArticleEntity> pendingArticles = List.of(articleEntity1, articleEntity2);

        when(articleRepository.findPendingArticles()).thenReturn(pendingArticles);

        DisplayArticleDTO dto1 = new DisplayArticleDTO();
        dto1.setId(1L);
        dto1.setTitle("Article 1");

        DisplayArticleDTO dto2 = new DisplayArticleDTO();
        dto2.setId(2L);
        dto2.setTitle("Article 2");

        when(modelMapper.map(articleEntity1, DisplayArticleDTO.class)).thenReturn(dto1);
        when(modelMapper.map(articleEntity2, DisplayArticleDTO.class)).thenReturn(dto2);

        List<DisplayArticleDTO> result = articleService.getAllPending();

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    void countPendingArticles_ReturnsCorrectCount() {
        int expectedCount = 3;
        when(articleRepository.countByStatus(Status.PENDING)).thenReturn(expectedCount);

        int actualCount = articleService.countPendingArticles();

        assertEquals(expectedCount, actualCount);
    }

    @Test
    void getAllByCategory_ReturnsMappedArticles_WhenCategoryExistsAndHasArticles() {

        String categoryName = "Science";

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryName);

        ArticleEntity article1 = new ArticleEntity();
        article1.setId(1L);
        article1.setTitle("Article 1");

        ArticleEntity article2 = new ArticleEntity();
        article2.setId(2L);
        article2.setTitle("Article 2");

        categoryEntity.setArticles(List.of(article1, article2));

        when(categoryService.getByName(categoryName)).thenReturn(Optional.of(categoryEntity));

        DisplayArticleDTO dto1 = new DisplayArticleDTO();
        dto1.setId(1L);
        dto1.setTitle("Article 1");

        DisplayArticleDTO dto2 = new DisplayArticleDTO();
        dto2.setId(2L);
        dto2.setTitle("Article 2");

        when(modelMapper.map(article1, DisplayArticleDTO.class)).thenReturn(dto1);
        when(modelMapper.map(article2, DisplayArticleDTO.class)).thenReturn(dto2);

        List<DisplayArticleDTO> result = articleService.getAllByCategory(categoryName);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    void getAllByCategory_ReturnsEmptyList_WhenCategoryDoesNotExist() {
        String categoryName = "NonExistentCategory";
        when(categoryService.getByName(categoryName)).thenReturn(Optional.empty());

        List<DisplayArticleDTO> result = articleService.getAllByCategory(categoryName);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByCategory_ReturnsEmptyList_WhenCategoryHasNoArticles() {
        String categoryName = "EmptyCategory";

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryName);
        categoryEntity.setArticles(List.of());

        when(categoryService.getByName(categoryName)).thenReturn(Optional.of(categoryEntity));

        List<DisplayArticleDTO> result = articleService.getAllByCategory(categoryName);

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserFavourites_ReturnsMappedFavouriteArticles_WhenUserExists() {
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();

        ArticleEntity article1 = new ArticleEntity();
        article1.setId(1L);
        article1.setTitle("Favourite Article 1");

        ArticleEntity article2 = new ArticleEntity();
        article2.setId(2L);
        article2.setTitle("Favourite Article 2");

        userEntity.setFavourites(List.of(article1, article2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        DisplayArticleDTO dto1 = new DisplayArticleDTO();
        dto1.setId(1L);
        dto1.setTitle("Favourite Article 1");

        DisplayArticleDTO dto2 = new DisplayArticleDTO();
        dto2.setId(2L);
        dto2.setTitle("Favourite Article 2");

        when(modelMapper.map(article1, DisplayArticleDTO.class)).thenReturn(dto1);
        when(modelMapper.map(article2, DisplayArticleDTO.class)).thenReturn(dto2);

        List<DisplayArticleDTO> result = articleService.getUserFavourites(userId);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    void getUserFavourites_ReturnsEmptyList_WhenUserDoesNotExist() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        List<DisplayArticleDTO> result = articleService.getUserFavourites(userId);

        assertEquals(0, result.size());
    }

    @Test
    void getUserFavourites_UserExistsWithNoFavorites_ReturnsEmptyList() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFavourites(Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        List<DisplayArticleDTO> result = articleService.getUserFavourites(userId);

        assertTrue(result.isEmpty());
    }
}
