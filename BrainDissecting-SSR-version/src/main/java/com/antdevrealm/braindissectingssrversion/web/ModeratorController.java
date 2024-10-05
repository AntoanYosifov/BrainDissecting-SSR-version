package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.ModeratorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    private final ArticleService articleService;
    private final ModeratorService moderatorService;

    public ModeratorController(ArticleService articleService, ModeratorService moderatorService) {
        this.articleService = articleService;
        this.moderatorService = moderatorService;
    }

    @GetMapping("/pending-for-approval")
    public String viewApproveArticles(Model model) {
        model.addAttribute("pendingArticles", articleService.getAllPending());
        return "pending-for-approval";
    }

    @PatchMapping("/approve/{articleId}")
    public String approveArticle(@PathVariable Long articleId) {
        boolean isApproved = moderatorService.approveArticle(articleId);

        if (!isApproved) {
            return "redirect:/moderator/pending-for-approval?error=Could not approve the article";
        }

        return "redirect:/moderator/pending-for-approval?success=Article approved";
    }

    @DeleteMapping("/reject/{articleId}")
    public String rejectArticle(@PathVariable Long articleId) {
        boolean isRejected = moderatorService.rejectArticle(articleId);

        if (!isRejected) {
            return "redirect:/moderator/pending-for-approval?error=Could not reject the article";
        }

        return "redirect:/moderator/pending-for-approval?success=Article rejected";
    }

}
