package com.antdevrealm.braindissectingssrversion;

import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRoleRepository;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final ArticleService articleService;

    private final UserService userService;

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    public Runner(ArticleService articleService, UserService userService, UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.articleService = articleService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
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
