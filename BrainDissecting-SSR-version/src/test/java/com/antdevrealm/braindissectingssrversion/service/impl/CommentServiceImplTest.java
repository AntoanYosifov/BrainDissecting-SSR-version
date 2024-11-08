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

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private ArticleRepository mockArticleRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Captor
    private ArgumentCaptor<CommentEntity> commentCaptor;

    private CommentServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new CommentServiceImpl(mockCommentRepository, mockArticleRepository, mockUserRepository);
    }

    @Test
    void add_ShouldSaveCommentAndReturnItsId_WhenAuthorAndArticleExist() {
        long authorId = 1L;
        long articleId = 1L;
        long expectedCommentId = 10L;
        AddCommentDTO addCommentDTO = new AddCommentDTO();
        addCommentDTO.setContent("testContent");

        UserEntity userEntity = new UserEntity();
        ArticleEntity articleEntity = new ArticleEntity();
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setId(expectedCommentId);

        when(mockUserRepository.findById(authorId)).thenReturn(Optional.of(userEntity));
        when(mockArticleRepository.findById(authorId)).thenReturn(Optional.of(articleEntity));
        when(mockCommentRepository.save(Mockito.any(CommentEntity.class))).thenReturn(commentEntity);

        long actualSavedCommentId = toTest.add(addCommentDTO, authorId, articleId);

        verify(mockCommentRepository).save(commentCaptor.capture());
        CommentEntity savedComment = commentCaptor.getValue();

        Assertions.assertEquals(expectedCommentId, actualSavedCommentId);
        Assertions.assertEquals(addCommentDTO.getContent(), savedComment.getContent());
        Assertions.assertEquals(userEntity, savedComment.getUser());
        Assertions.assertEquals(articleEntity, savedComment.getArticle());
    }

    @Test
    void add_ShouldReturnNegativeOne_WhenArticleNotFound() {
        AddCommentDTO addCommentDTO = new AddCommentDTO();
        long authorId = 1L;
        long articleId = 1L;

        Mockito.when(mockArticleRepository.findById(articleId)).thenReturn(Optional.empty());

        long result = toTest.add(addCommentDTO, authorId, articleId);

        Assertions.assertEquals(-1, result);
    }

    @Test
    void add_ShouldReturnNegativeTwo_WhenUserNotFound() {
        AddCommentDTO addCommentDTO = new AddCommentDTO();
        long authorId = 1L;
        long articleId = 1L;

        Mockito.when(mockArticleRepository.findById(articleId)).thenReturn(Optional.of(new ArticleEntity()));
        Mockito.when(mockUserRepository.findById(authorId)).thenReturn(Optional.empty());

        long result = toTest.add(addCommentDTO, authorId, articleId);

        Assertions.assertEquals(-2, result);
    }

}
