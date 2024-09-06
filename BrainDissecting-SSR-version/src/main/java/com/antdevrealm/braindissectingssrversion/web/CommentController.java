package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.AddCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/articles/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String addCommentToArticle(AddCommentDTO addCommentDTO,
                                      @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails,
                                      @PathVariable Long articleId
    ) {

        long commentId = commentService.addComment(addCommentDTO, brDissectingUserDetails.getId(), articleId);

        // Add validation for the Authentication principal. Redirect to login if the current user is anonymous!
        return "redirect:/articles/all?open=" + articleId + "#comment-" + commentId;
    }
}
