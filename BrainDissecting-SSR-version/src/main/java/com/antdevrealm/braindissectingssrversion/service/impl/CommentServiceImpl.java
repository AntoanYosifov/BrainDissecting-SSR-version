package com.antdevrealm.braindissectingssrversion.service.impl;

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
    public long add(AddCommentDTO addCommentDTO , long authorId, long articleId) {
        Optional<ArticleEntity> optionalArt = articleRepository.findById(articleId);

        if(optionalArt.isEmpty()) {
            return -1;
        }

        Optional<UserEntity> optUser = userRepository.findById(authorId);

        if(optUser.isEmpty()) {
            return -2;
        }

        UserEntity userEntity = optUser.get();
        ArticleEntity articleEntity = optionalArt.get();

        CommentEntity commentEntity = mapToComment(addCommentDTO, userEntity, articleEntity);

        return commentRepository.save(commentEntity).getId();
    }

    @Override
    @Modifying
    @Transactional
    public boolean delete(long articleId, long commentId, long userId) {
        Optional<ArticleEntity> optionalArt = articleRepository.findById(articleId);

        if(optionalArt.isEmpty()) {
            return false;
        }

        ArticleEntity articleEntity = optionalArt.get();

        Optional<UserEntity> optUser = userRepository.findById(userId);

        if(optUser.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();

        Optional<CommentEntity> commentById = commentRepository.findById(commentId);

        if(commentById.isEmpty()) {
            return false;
        }

        CommentEntity commentEntity = commentById.get();

        commentRepository.delete(commentEntity);

        userEntity.getComments().remove(commentEntity);
        articleEntity.getComments().remove(commentEntity);

        return true;
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
