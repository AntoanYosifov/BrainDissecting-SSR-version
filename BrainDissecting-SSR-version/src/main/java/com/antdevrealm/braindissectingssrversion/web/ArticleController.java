package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {


    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    public ModelAndView viewAllArticles(ModelAndView mnv) {

        List<DisplayArticleDTO> allArticles = articleService.getAllArticles();

        mnv.addObject("allArticles", allArticles);
        mnv.setViewName("articles");
        return mnv;
    }

}
