package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private final ArticleService articleService;

    public HomeController(ArticleService articleService) {
        this.articleService = articleService;

    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (userDetails instanceof BrDissectingUserDetails brDissectingUserDetails) {
            model.addAttribute("welcomeMessage", brDissectingUserDetails.getFullName());
            model.addAttribute("favourites", articleService.getUserFavourites(brDissectingUserDetails.getId()));
            return "home";
        }

        model.addAttribute("welcomeMessage", "Anonymous");
        return "index";
    }
}
