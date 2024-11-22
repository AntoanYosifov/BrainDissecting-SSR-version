package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void resetUserRepository() {
        userRepository.deleteAll();
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
        setAuthenticatedUser("testUser", 1L, false);

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void viewProfile_ShouldRedirectToBannedView_WhenUserIsBanned() throws Exception {
        setAuthenticatedUser("bannedUser", 2L, true);

        mockMvc.perform(get("/users/profile"))
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
        setAuthenticatedUser("testUser", 1L, false);

        mockMvc.perform(patch("/users/profile/update")
                .param("newUsername" , "newUsername")
                .param("confirmUsername", "newUsername")
                .param("newEmail", "newemail@example.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/logout"));
    }

    @Test
    void update_ShouldRedirectToProfileWithValidationErrors_WhenBindingResultHasErrors() throws Exception {
        setAuthenticatedUser("testUser", 1L, false);

        mockMvc.perform(patch("/users/profile/update")
                        .param("newUsername", "")
                        .param("confirmUsername", "differentUsername")
                        .param("newEmail", "invalid-email")
                        .with(csrf()))
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
        setAuthenticatedUser("testUser", user.getId(), false);

        mockMvc.perform(patch("/users/profile/update")
                        .param("newUsername", "newUsername")
                        .param("confirmUsername", "differentUsername")
                        .param("newEmail", "validemail@example.com")
                        .with(csrf()))
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
        setAuthenticatedUser("testUser", user.getId(), false);

        mockMvc.perform(patch("/users/profile/update")
                        .param("newUsername", "updatedUsername")
                        .param("confirmUsername", "updatedUsername")
                        .param("newEmail", "updatedemail@example.com")
                        .with(csrf()))
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
        setAuthenticatedUser("testUser", user.getId(), false);

        mockMvc.perform(get("/users/favourites"))
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

        userRepository.saveAndFlush(user);
        setAuthenticatedUser("testUser", user.getId(), false);

        mockMvc.perform(post("/users/add-to-favourites/{articleId}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all"));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

}
