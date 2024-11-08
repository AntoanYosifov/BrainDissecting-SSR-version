package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.dto.comment.AddCommentDTO;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
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
                                      @PathVariable Long articleId) {

        long commentId = commentService.add(addCommentDTO, brDissectingUserDetails.getId(), articleId);

        if (commentId == -1) {
            return "redirect:/articles/all?error=article_not_found&articleId=" + articleId;
        } else if (commentId == -2) {
            return "redirect:/articles/all?error=user_not_found&articleId=" + articleId;
        }

        return "redirect:/articles/all?open=" + articleId + "#comment-" + commentId;
    }

    @DeleteMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long articleId , @PathVariable Long commentId,
                                @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        boolean success = commentService.delete(articleId, commentId, brDissectingUserDetails.getId());

        return "redirect:/articles/all";

    }
}
