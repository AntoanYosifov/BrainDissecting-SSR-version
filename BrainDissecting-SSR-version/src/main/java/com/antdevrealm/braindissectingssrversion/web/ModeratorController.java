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
        moderatorService.approveArticle(articleId);
        return "redirect:/moderator/pending-for-approval";
    }

    @DeleteMapping("/reject/{articleId}")
    public String rejectArticle(@PathVariable Long articleId) {
        moderatorService.rejectArticle(articleId);
        return "redirect:/moderator/pending-for-approval";
    }

}
