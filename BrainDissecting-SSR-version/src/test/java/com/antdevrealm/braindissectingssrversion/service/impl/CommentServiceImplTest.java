package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.AddCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.CommentRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long ARTICLE_ID = 1L;
    private static final long COMMENT_ID = 1L;

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private ArticleRepository mockArticleRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Captor
    private ArgumentCaptor<CommentEntity> commentCaptor;

    private AddCommentDTO addCommentDTO;
    private UserEntity userEntity;
    private ArticleEntity articleEntity;
    private CommentEntity commentEntity;

    private CommentServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new CommentServiceImpl(mockCommentRepository, mockArticleRepository, mockUserRepository);

        addCommentDTO = new AddCommentDTO();
        addCommentDTO.setContent("testContent");

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setUsername("testUser");

        articleEntity = new ArticleEntity();
        articleEntity.setId(ARTICLE_ID);
        articleEntity.setTitle("testArticle");

        commentEntity = new CommentEntity();
        commentEntity.setId(COMMENT_ID);
        commentEntity.setContent("testContent");
    }

    @Test
    void add_ShouldSaveCommentAndReturnItsId_WhenUserAndArticleExist() {
        long expectedCommentId = 10L;
        commentEntity.setId(expectedCommentId);

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));
        when(mockCommentRepository.save(Mockito.any(CommentEntity.class))).thenReturn(commentEntity);

        long actualSavedCommentId = toTest.add(addCommentDTO, USER_ID, ARTICLE_ID);

        verify(mockCommentRepository).save(commentCaptor.capture());
        CommentEntity savedComment = commentCaptor.getValue();

        Assertions.assertEquals(expectedCommentId, actualSavedCommentId);
        Assertions.assertEquals(addCommentDTO.getContent(), savedComment.getContent());
        Assertions.assertEquals(userEntity, savedComment.getUser());
        Assertions.assertEquals(articleEntity, savedComment.getArticle());
    }

    @Test
    void add_ShouldReturnNegativeOne_WhenArticleNotFound() {
        Mockito.when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        long result = toTest.add(addCommentDTO, USER_ID, ARTICLE_ID);

        Assertions.assertEquals(-1, result);
    }

    @Test
    void add_ShouldReturnNegativeTwo_WhenUserNotFound() {
        Mockito.when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(new ArticleEntity()));
        Mockito.when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

        long result = toTest.add(addCommentDTO, USER_ID, ARTICLE_ID);

        Assertions.assertEquals(-2, result);
    }

    @Test
    void delete_ShouldReturnTrue_WhenArticleCommentAndUserExist() {
        articleEntity.getComments().add(commentEntity);
        userEntity.getComments().add(commentEntity);

        Assertions.assertTrue(articleEntity.getComments().contains(commentEntity));
        Assertions.assertTrue(userEntity.getComments().contains(commentEntity));

        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));
        when(mockCommentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentEntity));
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.delete(ARTICLE_ID, COMMENT_ID, USER_ID);

        Assertions.assertTrue(result);
        Assertions.assertFalse(articleEntity.getComments().contains(commentEntity));
        Assertions.assertFalse(userEntity.getComments().contains(commentEntity));
    }

    @Test
    void delete_ShouldReturnFalse_WhenArticleDoesNotExist() {
        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        boolean result = toTest.delete(ARTICLE_ID, COMMENT_ID, USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void delete_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

        boolean result = toTest.delete(ARTICLE_ID, COMMENT_ID, USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void delete_ShouldReturnFalse_WhenUserCommentNotExist() {
        when(mockArticleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockCommentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        boolean result = toTest.delete(ARTICLE_ID, COMMENT_ID, USER_ID);

        Assertions.assertFalse(result);
    }

}