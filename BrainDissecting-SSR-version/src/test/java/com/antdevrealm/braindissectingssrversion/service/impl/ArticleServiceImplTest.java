package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        // Arrange: create a list of ArticleEntity objects
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

        // Map ArticleEntity to DisplayArticleDTO using mock
        DisplayArticleDTO dto1 = new DisplayArticleDTO();
        dto1.setId(1L);
        dto1.setTitle("Article 1");

        DisplayArticleDTO dto2 = new DisplayArticleDTO();
        dto2.setId(2L);
        dto2.setTitle("Article 2");

        when(modelMapper.map(articleEntity1, DisplayArticleDTO.class)).thenReturn(dto1);
        when(modelMapper.map(articleEntity2, DisplayArticleDTO.class)).thenReturn(dto2);


        // Act
        List<DisplayArticleDTO> result = articleService.getAllApproved();

        // Assert: verify the size and contents
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }
}
