package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.model.dto.article.ArticleDTO;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {


    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    public String viewAllArticles(Model model) {

        List<ArticleDTO> allArticles = articleService.getAllArticles();

        model.addAttribute("allArticles", allArticles);
        return "articles";
    }

}
