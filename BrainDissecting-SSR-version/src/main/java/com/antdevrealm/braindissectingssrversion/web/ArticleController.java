package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.model.dto.article.DisplayArticleDTO;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {


    private final ArticleService articleService;
    private final UserService userService;


    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String viewAllArticles(Model model,
                                  @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        List<DisplayArticleDTO> allArticles = articleService.getAllApproved();
        List<Long> favouriteArticlesIds = userService.getFavouriteArticlesIds(brDissectingUserDetails.getId());

        model.addAttribute("allArticles", allArticles);
        model.addAttribute("favouriteArtIds", favouriteArticlesIds);
        model.addAttribute("currentUserId", brDissectingUserDetails.getId());

        return "articles-all";
    }

//    @GetMapping("/upload")
//    public String testViewUpload() {
//        return "offer-add";
//    }

    @GetMapping("/category/{categoryName}")
    public String viewCategory(@PathVariable String categoryName, Model model,
                               @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        List<DisplayArticleDTO> byCategory = articleService.getAllByCategory(categoryName);
        List<Long> favouriteArticlesIds = userService.getFavouriteArticlesIds(brDissectingUserDetails.getId());

        model.addAttribute("byCategory", byCategory);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("favouriteArtIds", favouriteArticlesIds);
        model.addAttribute("currentUserId", brDissectingUserDetails.getId());
        return "articles-by-category";
    }

}
