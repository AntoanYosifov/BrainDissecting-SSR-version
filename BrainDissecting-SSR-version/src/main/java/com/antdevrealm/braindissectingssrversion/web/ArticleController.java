package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {


    private final ArticleService articleService;
    private final CommentService commentService;

    public ArticleController(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @GetMapping("/all")
    public String viewAllArticles(Model model,
                                  @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        List<DisplayArticleDTO> allArticles = articleService.getAllArticles();

        model.addAttribute("allArticles", allArticles);
        model.addAttribute("currentUserId", brDissectingUserDetails.getId());
        return "articles";
    }

    @GetMapping("/upload")
    public String testViewUpload() {
        return "offer-add";
    }

}
