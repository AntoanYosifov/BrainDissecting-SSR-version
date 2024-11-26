package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.exception.ArticleNotFoundException;
import com.antdevrealm.braindissectingssrversion.exception.CommentNotFoundException;
import com.antdevrealm.braindissectingssrversion.exception.UserNotFoundException;
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

       try {
           long commentId = commentService.add(addCommentDTO, brDissectingUserDetails.getId(), articleId);
           return "redirect:/articles/all?open=" + articleId + "#comment-" + commentId;
       } catch (ArticleNotFoundException e) {
           return "redirect:/articles/all?error=add_article_not_found";
       } catch (UserNotFoundException e) {
           return "redirect:/articles/all?error=user_not_found";
       }

    }

    @DeleteMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long articleId , @PathVariable Long commentId,
                                @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        try {
            commentService.delete(articleId, commentId, brDissectingUserDetails.getId());
        } catch (ArticleNotFoundException e) {
            return "redirect:/articles/all?error=delete_article_not_found";
        } catch (UserNotFoundException e) {
            return "redirect:/articles/all?error=user_not_found";
        } catch (CommentNotFoundException e) {
            return "redirect:/articles/all?error=comment_not_found";
        }

        return "redirect:/articles/all";
    }
}
