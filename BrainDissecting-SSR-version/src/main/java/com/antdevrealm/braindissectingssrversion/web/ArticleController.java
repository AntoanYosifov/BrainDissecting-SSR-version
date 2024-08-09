package com.antdevrealm.braindissectingssrversion.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @GetMapping("/all")
    public ModelAndView viewAllArticles(ModelAndView mnv) {

        mnv.setViewName("articles");
        return mnv;
    }

}
