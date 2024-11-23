package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ArticleRepository articleRepository;

    private BrDissectingUserDetails userDetails;
    private UsernamePasswordAuthenticationToken authenticationToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userDetails = new BrDissectingUserDetails(
                1L,
                "testUser@example.com",
                "testUser",
                "password",
                List.of(() -> "ROLE_USER"),
                "Test",
                "User",
                false
        );

        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

    }

    private void setAuthenticatedUser(String username, Long id, boolean isBanned) {
        BrDissectingUserDetails userDetails = new BrDissectingUserDetails(
                id,
                username + "@example.com",
                username,
                "password",
                List.of(() -> "ROLE_USER"),
                "Test",
                "User",
                isBanned
        );

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void viewProfile_ShouldReturnMyProfileViewWhenUserIsActive() throws Exception {
        mockMvc.perform(get("/users/profile")
                        .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void viewProfile_ShouldRedirectToBannedView_WhenUserIsBanned() throws Exception {
        userDetails.setBanned(true);

        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        mockMvc.perform(get("/users/profile")
                        .with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/banned"));
    }

    @Test
    void viewProfile_ShouldRedirectToLogin_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/users/login"));
    }

    @Test
    void update_ShouldRedirectToLogout_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(patch("/users/profile/update")
                        .param("newUsername", "newUsername")
                        .param("confirmUsername", "newUsername")
                        .param("newEmail", "newemail@example.com")
                        .with(csrf())
                        .with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/logout"));
    }

    @Test
    void update_ShouldRedirectToProfileWithValidationErrors_WhenBindingResultHasErrors() throws Exception {
        mockMvc.perform(patch("/users/profile/update")
                        .param("newUsername", "")
                        .param("confirmUsername", "differentUsername")
                        .param("newEmail", "invalid-email")
                        .with(csrf())
                        .with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"))
                .andExpect(flash().attributeExists("updateData"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.updateData"))
                .andExpect(flash().attribute("editProfile", true));
    }

    @Test
    void update_ShouldRedirectToProfileWithError_WhenUsernameAndConfirmUsernameDoNotMatch() throws Exception {
        UserEntity user = new UserEntity()
                .setUsername("testUser")
                .setEmail("testUser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(user);

        userDetails.setId(user.getId());
        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());


        mockMvc.perform(patch("/users/profile/update")
                        .param("newUsername", "newUsername")
                        .param("confirmUsername", "differentUsername")
                        .param("newEmail", "validemail@example.com")
                        .with(csrf())
                        .with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"))
                .andExpect(flash().attributeExists("usernameConfUsernameMissMatch"));
    }

    @Test
    void update_ShouldRedirectToProfileWithSuccessMessage_WhenUpdateSucceeds() throws Exception {
        UserEntity user = new UserEntity()
                .setUsername("testUser")
                .setEmail("testUser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(user);

        userDetails.setId(user.getId());
        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());


        mockMvc.perform(patch("/users/profile/update")
                        .param("newUsername", "updatedUsername")
                        .param("confirmUsername", "updatedUsername")
                        .param("newEmail", "updatedemail@example.com")
                        .with(csrf())
                        .with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "You successfully updated your profile info!"));

        Optional<UserEntity> updatedUserOpt = userRepository.findByUsername("updatedUsername");
        Assertions.assertTrue(updatedUserOpt.isPresent());
        UserEntity updatedUser = updatedUserOpt.get();

        Assertions.assertEquals("updatedUsername", updatedUser.getUsername());
        Assertions.assertEquals("updatedemail@example.com", updatedUser.getEmail());
    }

    @Test
    void viewFavourites_ShouldReturnUserFavorites() throws Exception {
        UserEntity user = new UserEntity()
                .setUsername("testUser")
                .setEmail("testUser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(user);

        userDetails.setId(user.getId());
        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        mockMvc.perform(get("/users/favourites").with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("user-favorites"))
                .andExpect(model().attributeExists("favourites"))
                .andExpect(model().attributeExists("currentUserId"));
    }

    @Test
    void addToFavourites_ShouldRedirectToAllArticles_WhenArticleIsAdded() throws Exception {
        UserEntity user = new UserEntity()
                .setUsername("testUser")
                .setEmail("testUser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        userRepository.saveAndFlush(user);
        articleRepository.saveAndFlush(articleEntity);

        userDetails.setId(user.getId());
        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        Optional<ArticleEntity> optArticle = articleRepository.findByTitle("testTitle");

        Assertions.assertTrue(optArticle.isPresent());

        ArticleEntity savedArticle = optArticle.get();

        mockMvc.perform(post("/users/add-to-favourites/{articleId}", savedArticle.getId())
                        .with(csrf())
                        .with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all"));
    }

    @Test
    void removeFromFavourites_ShouldRedirectToUsersFavourites_WhenArticleIsRemoved() throws Exception {
        UserEntity user = new UserEntity()
                .setUsername("testUser")
                .setEmail("testUser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);
        user.setId(1L);
        userRepository.saveAndFlush(user);

        UserEntity savedUser = userRepository.findByUsername("testUser").orElseThrow();
        setAuthenticatedUser("testUser", savedUser.getId(), false);

        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);
        articleRepository.saveAndFlush(articleEntity);

        ArticleEntity savedArticle = articleRepository.findByTitle("testTitle").orElseThrow();

        userDetails.setId(savedUser.getId());
        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        userService.addArticleToFavourites(savedArticle.getId(), savedUser.getId());

        Assertions.assertTrue(savedUser.getFavourites().contains(savedArticle));

        mockMvc.perform(delete("/users/remove-from-favourites/{articleId}", savedArticle.getId())
                        .with(csrf())
                        .with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/favourites"));

        Assertions.assertFalse(savedUser.getFavourites().contains(savedArticle));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        articleRepository.deleteAll();
    }

}
