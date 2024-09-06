package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.AddCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.CommentRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.CommentService;
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
    public boolean addComment(AddCommentDTO addCommentDTO ,long authorId, long articleId) {
        Optional<ArticleEntity> optionalArt = articleRepository.findById(articleId);

        if(optionalArt.isEmpty()) {
            return false;
        }

        Optional<UserEntity> optUser = userRepository.findById(authorId);

        if(optUser.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();
        ArticleEntity articleEntity = optionalArt.get();

        CommentEntity commentEntity = mapToComment(addCommentDTO, userEntity, articleEntity);

        commentRepository.save(commentEntity);

        return true;
    }

    private static CommentEntity mapToComment(AddCommentDTO addCommentDTO,
                                              UserEntity author,
                                              ArticleEntity article
                                              ) {

        CommentEntity commentEntity = new CommentEntity();

        commentEntity.setContent(addCommentDTO.getContent())
                .setUser(author)
                .setArticle(article);

        return commentEntity;
    }
}
