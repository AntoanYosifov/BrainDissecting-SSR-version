package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    private final ArticleService articleService;

    public ModeratorController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/articles-approval")
    public String viewApproveArticles(Model model) {

        model.addAttribute("pendingArticles", articleService.getAllPending());
        return "articles-approval";
    }

}
