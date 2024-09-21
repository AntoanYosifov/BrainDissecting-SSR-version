package com.antdevrealm.braindissectingssrversion;

import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
//        List<ArticleEntity> allEntity = articleRepository.findAll();
//
//        List<DisplayArticleDTO> allDTO = articleService.getAllArticles();
//
//        System.out.println();


//        articleService.updateCategories();
//
//        Optional<UserEntity> user = userRepository.findById(1L);
//
//        UserEntity userEntity = user.get();
//
//
//        List<UserRoleEntity> roles = userEntity.getRoles();
//
//
//        Optional<UserRoleEntity> optRole = userRoleRepository.findByRole(UserRole.ADMIN);
//
//        UserRoleEntity admin = optRole.get();
//
//
//        userEntity.getRoles().add(admin);
//
//        userRepository.save(userEntity);
////
////
//        System.out.println();
////        List<DisplayUserInfoDTO> allUsers = userService.getAllUsers();
////
////        System.out.println();

    }
}
