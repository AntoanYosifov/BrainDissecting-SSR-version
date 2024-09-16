package com.antdevrealm.braindissectingssrversion;

import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final ArticleService articleService;

    public Runner(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void run(String... args) throws Exception {
//        articleService.updateCategories();
    }
}
