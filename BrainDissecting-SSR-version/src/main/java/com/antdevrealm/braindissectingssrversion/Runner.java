package com.antdevrealm.braindissectingssrversion;

import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Runner implements CommandLineRunner {

    private final ArticleService articleService;

    public Runner(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void run(String... args) throws Exception {
//        articleService.updateArticles();
//        System.out.println();

//        double[] numbers = new double[]{1, 2, 3, 4, 5};
//
//        numbers[0] = 2;
//
//        System.out.println(Arrays.toString(numbers));
    }
}
