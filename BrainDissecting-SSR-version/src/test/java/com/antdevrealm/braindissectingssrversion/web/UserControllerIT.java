package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.util.annotation.WithCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithCustomUser(username = "testUser", roles = {"USER"}, banned = false)
    void viewProfile_ShouldReturnMyProfileViewWhenUserIsActive() throws Exception {
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithCustomUser(username = "bannedUser", roles = {"USER"}, banned = true)
    void viewProfile_ShouldRedirectToBannedView_WhenUserIsBanned() throws Exception {
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
    @WithCustomUser(username = "testUser", roles = {"USER"}, banned = false)
    void update_ShouldRedirectToLogout_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(patch("/users/profile/update")
                .param("newUsername" , "newUsername")
                .param("confirmUsername", "newUsername")
                .param("newEmail", "newemail@example.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/logout"));
    }

    @Test
    @WithCustomUser(username = "testUser", roles = {"USER"}, banned = false)
    void update_ShouldRedirectToProfileWithValidationErrors_WhenBindingResultHasErrors() throws Exception {
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

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

}
