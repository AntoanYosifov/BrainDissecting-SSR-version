package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.AddCommentDTO;

public interface CommentService {
    boolean addComment(AddCommentDTO addCommentDTO, long authorId, long articleId);
}
