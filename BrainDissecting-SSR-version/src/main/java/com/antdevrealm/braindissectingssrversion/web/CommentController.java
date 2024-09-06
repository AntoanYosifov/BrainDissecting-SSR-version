package com.antdevrealm.braindissectingssrversion.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/articles/{articleId}/comments")
public class CommentController {

    @PostMapping
    public String addCommentToArticle(@PathVariable Long articleId) {


        return "redirect:/articles/all";
    }
}
