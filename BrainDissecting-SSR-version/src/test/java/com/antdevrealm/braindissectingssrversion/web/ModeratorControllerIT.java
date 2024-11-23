package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.ThemeSuggestionEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.ThemeSuggestionRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ModeratorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeSuggestionRepository themeSuggestionRepository;

    private BrDissectingUserDetails moderatorDetails;
    private UsernamePasswordAuthenticationToken moderatorAuthenticationToken;

    private BrDissectingUserDetails nonModeratorDetails;
    private UsernamePasswordAuthenticationToken nonModeratorAuthenticationToken;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
        themeSuggestionRepository.deleteAll();

        moderatorDetails = new BrDissectingUserDetails(
                1L,
                "testUser@example.com",
                "testUser",
                "password",
                List.of(() -> "ROLE_MODERATOR"),
                "Test",
                "User",
                false
        );

        moderatorAuthenticationToken = new UsernamePasswordAuthenticationToken(
                moderatorDetails, null, moderatorDetails.getAuthorities());

        nonModeratorDetails = new BrDissectingUserDetails(
                1L,
                "testUser@example.com",
                "testUser",
                "password",
                List.of(() -> "ROLE_USER"),
                "Test",
                "User",
                false
        );

        nonModeratorAuthenticationToken = new UsernamePasswordAuthenticationToken(
                nonModeratorDetails, null, nonModeratorDetails.getAuthorities());

    }

    @Test
    void viewApproveArticleShouldReturnPendingForApproval() throws Exception {
        mockMvc.perform(get("/moderator/pending-for-approval")
                        .with(authentication(moderatorAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("pending-for-approval"))
                .andExpect(model().attributeExists("pendingArticles"));
    }

    @Test
    void viewApproveArticleShouldRedirectToAccessDenied_WhenUserIsNotModerator() throws Exception {
        mockMvc.perform(get("/moderator/pending-for-approval")
                        .with(authentication(nonModeratorAuthenticationToken)))
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
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?success=Article approved"));
    }

    @Test
    void approveArticle_ShouldRedirectToError_WhenArticleDoesNotExist() throws Exception {
        mockMvc.perform(patch("/moderator/approve/9999")
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?error=Could not approve the article"));
    }

    @Test
    void approveArticle_ShouldRedirectToError_WhenArticleIsNotPending() throws Exception {
        ArticleEntity approvedArticle = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        articleRepository.saveAndFlush(approvedArticle);

        mockMvc.perform(patch("/moderator/approve/{articleId}", approvedArticle.getId())
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?error=Could not approve the article"));
    }

    @Test
    void rejectArticle_ShouldRejectArticleAndRedirectToSuccess_WhenArticleIsPendingAndUserIsModerator() throws Exception {
        ArticleEntity pendingArticle = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.PENDING);

        articleRepository.saveAndFlush(pendingArticle);

        mockMvc.perform(delete("/moderator/reject/{articleId}", pendingArticle.getId())
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?success=Article rejected"));
    }

    @Test
    void rejectArticle_ShouldRedirectToError_WhenArticleDoesNotExist() throws Exception {
        mockMvc.perform(delete("/moderator/reject/9999")
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?error=Could not reject the article"));
    }

    @Test
    void rejectArticle_ShouldRedirectToError_WhenArticleIsNotPending() throws Exception {
        ArticleEntity approvedArticle = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        articleRepository.saveAndFlush(approvedArticle);

        mockMvc.perform(delete("/moderator/reject/{articleId}", approvedArticle.getId())
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?error=Could not reject the article"));
    }

    @Test
    void viewSuggestThemes_ShouldReturnSuggestThemesViewWithModel_WhenUserIsModerator() throws Exception {
        UserEntity userEntity = new UserEntity()
                .setUsername("testUser")
                .setEmail("testUser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);
        userEntity.setId(1L);
        userRepository.saveAndFlush(userEntity);

        ThemeSuggestionEntity suggestion1 = new ThemeSuggestionEntity()
                .setName("Theme 1")
                .setSuggestedBy(userEntity);
        ThemeSuggestionEntity suggestion2 = new ThemeSuggestionEntity()
                .setName("Theme 2")
                .setSuggestedBy(userEntity);

        themeSuggestionRepository.saveAll(List.of(suggestion1, suggestion2));

        moderatorDetails.setId(userEntity.getId());

        mockMvc.perform(get("/moderator/suggest-themes")
                        .with(authentication(moderatorAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(view().name("suggest-themes"))
                .andExpect(model().attributeExists("suggestedThemes"))
                .andExpect(model().attribute("suggestedThemes", hasSize(2)));
    }

    @Test
    void viewSuggestThemes_ShouldRedirectToAccessDenied_WhenUserIsNotModerator() throws Exception {
        mockMvc.perform(get("/moderator/suggest-themes")
                        .with(authentication(nonModeratorAuthenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }


    @AfterEach
    void cleanUp() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
        themeSuggestionRepository.deleteAll();
    }

}
