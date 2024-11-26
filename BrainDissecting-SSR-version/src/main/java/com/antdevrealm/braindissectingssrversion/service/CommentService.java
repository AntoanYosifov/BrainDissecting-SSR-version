package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.AddCommentDTO;

public interface CommentService {

    long add(AddCommentDTO addCommentDTO, long authorId, long articleId);

    void delete(long articleId, long commentId, long userId);
}
