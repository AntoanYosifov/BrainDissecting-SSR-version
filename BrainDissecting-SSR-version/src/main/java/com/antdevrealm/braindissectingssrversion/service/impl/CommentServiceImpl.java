package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.ArticleNotFoundException;
import com.antdevrealm.braindissectingssrversion.exception.CommentAuthorException;
import com.antdevrealm.braindissectingssrversion.exception.CommentNotFoundException;
import com.antdevrealm.braindissectingssrversion.exception.UserNotFoundException;
import com.antdevrealm.braindissectingssrversion.model.dto.comment.AddCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.CommentRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.CommentService;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, ArticleRepository articleRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }


    @Override
    public long add(AddCommentDTO addCommentDTO, long authorId, long articleId) {
        ArticleEntity articleEntity = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        UserEntity userEntity = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        CommentEntity commentEntity = mapToComment(addCommentDTO, userEntity, articleEntity);

        return commentRepository.save(commentEntity).getId();
    }

    @Override
    @Modifying
    @Transactional
    public void delete(long articleId, long commentId, long userId) {
        ArticleEntity articleEntity = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if(userId != commentEntity.getUser().getId()) {
            throw new CommentAuthorException(userId, commentId);
        }

        commentRepository.delete(commentEntity);

        userEntity.getComments().remove(commentEntity);
        articleEntity.getComments().remove(commentEntity);

    }

    private static CommentEntity mapToComment(AddCommentDTO addCommentDTO,
                                              UserEntity author,
                                              ArticleEntity article) {

        CommentEntity commentEntity = new CommentEntity();

        commentEntity.setContent(addCommentDTO.getContent())
                .setUser(author)
                .setArticle(article);

        return commentEntity;
    }

}
