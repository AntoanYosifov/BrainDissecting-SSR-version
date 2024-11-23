package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ModeratorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;

    private BrDissectingUserDetails userDetails;
    private UsernamePasswordAuthenticationToken authenticationToken;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();

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

    @Test
    void viewApproveArticles_ShouldRedirectToLogin_WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/moderator/pending-for-approval"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/users/login"));
    }

    @Test
    void approveArticle_ShouldApproveArticleAndRedirectToSuccess_WhenArticleIsPendingAndUserIsModerator() throws Exception {
        ArticleEntity pendingArticle = new ArticleEntity()
                        .setTitle("testTitle")
                        .setContent("testContent")
                        .setStatus(Status.PENDING);

        articleRepository.saveAndFlush(pendingArticle);

        mockMvc.perform(patch("/moderator/approve/{articleId}", pendingArticle.getId())
                .with(authentication(authenticationToken))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?success=Article approved"));
    }

    @Test
    void approveArticle_ShouldRedirectToError_WhenArticleDoesNotExist() throws Exception {
        mockMvc.perform(patch("/moderator/approve/9999")
                        .with(authentication(authenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?error=Could not approve the article"));
    }

    @AfterEach
    void cleanUp () {
        articleRepository.deleteAll();
    }

}
