package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ModeratorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private BrDissectingUserDetails userDetails;
    private UsernamePasswordAuthenticationToken authenticationToken;

    @BeforeEach
    void setUp() {
        userDetails = new BrDissectingUserDetails(
                1L,
                "testUser@example.com",
                "testUser",
                "password",
                List.of(() -> "ROLE_MODERATOR"),
                "Test",
                "User",
                false
        );

        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void viewApproveArticleShouldReturnPendingForApproval() throws Exception {
        mockMvc.perform(get("/moderator/pending-for-approval")
                .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("pending-for-approval"))
                .andExpect(model().attributeExists("pendingArticles"));
    }

    @Test
    void viewApproveArticleShouldRedirectToAccessDenied_WhenUserIsNotModerator() throws Exception {
        BrDissectingUserDetails nonModeratorUserDetails = new BrDissectingUserDetails(
                2L,
                "regularUser@example.com",
                "regularUser",
                "password",
                List.of(() -> "ROLE_USER"), // User does not have ROLE_MODERATOR
                "Regular",
                "User",
                false
        );

        UsernamePasswordAuthenticationToken nonModeratorAuthToken = new UsernamePasswordAuthenticationToken(
                nonModeratorUserDetails, null, nonModeratorUserDetails.getAuthorities());

        mockMvc.perform(get("/moderator/pending-for-approval")
                        .with(authentication(nonModeratorAuthToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

}
