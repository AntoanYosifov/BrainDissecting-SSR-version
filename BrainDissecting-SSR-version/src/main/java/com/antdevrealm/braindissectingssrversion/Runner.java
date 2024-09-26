package com.antdevrealm.braindissectingssrversion;

import com.antdevrealm.braindissectingssrversion.model.dto.article.FetchArticleDTO;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.jsoup.Jsoup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Runner implements CommandLineRunner {

    private final ArticleService articleService;

    private final ArticleRepository articleRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public Runner(ArticleService articleService, ArticleRepository articleRepository, UserService userService, UserRepository userRepository, RoleRepository roleRepository) {
        this.articleService = articleService;
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

//        articleService.updateArticles();

//        System.out.println();

//        List<FetchArticleDTO> dtos = articleService.fetchArticles("brain");

    }
}
