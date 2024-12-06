package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.ThemeSuggestionEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.CategoryRepository;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    private CategoryRepository categoryRepository;

    private UserEntity moderatorUserEntity;
    private BrDissectingUserDetails moderatorDetails;
    private UsernamePasswordAuthenticationToken moderatorAuthenticationToken;

    private UsernamePasswordAuthenticationToken nonModeratorAuthenticationToken;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
        themeSuggestionRepository.deleteAll();

        moderatorUserEntity = new UserEntity()
                .setUsername("moderatorUser")
                .setEmail("moderator@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(moderatorUserEntity);

        moderatorDetails = new BrDissectingUserDetails(
                moderatorUserEntity.getId(),
                moderatorUserEntity.getEmail(),
                moderatorUserEntity.getUsername(),
                moderatorUserEntity.getPassword(),
                List.of(() -> "ROLE_MODERATOR"),
                "Moderator",
                "User",
                false
        );

        moderatorAuthenticationToken = new UsernamePasswordAuthenticationToken(
                moderatorDetails, null, moderatorDetails.getAuthorities());

        UserEntity nonModeratorUserEntity = new UserEntity()
                .setUsername("nonModeratorUser")
                .setEmail("user@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);
        userRepository.saveAndFlush(nonModeratorUserEntity);

        BrDissectingUserDetails nonModeratorDetails = new BrDissectingUserDetails(
                nonModeratorUserEntity.getId(),
                nonModeratorUserEntity.getEmail(),
                nonModeratorUserEntity.getUsername(),
                nonModeratorUserEntity.getPassword(),
                List.of(() -> "ROLE_USER"),
                "NonModerator",
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
                .andExpect(redirectedUrl("/moderator/pending-for-approval?success=Article rejected!"));
    }

    @Test
    void rejectArticle_ShouldRedirectToError_WhenArticleDoesNotExist() throws Exception {
        mockMvc.perform(delete("/moderator/reject/9999")
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/pending-for-approval?error=Could not reject the article!"));
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
                .andExpect(redirectedUrl("/moderator/pending-for-approval?error=Could not reject the article!"));
    }

    @Test
    void viewSuggestThemes_ShouldReturnSuggestThemesViewWithModel_WhenUserIsModerator() throws Exception {
        ThemeSuggestionEntity suggestion1 = new ThemeSuggestionEntity()
                .setName("Theme 1")
                .setSuggestedBy(moderatorUserEntity);
        ThemeSuggestionEntity suggestion2 = new ThemeSuggestionEntity()
                .setName("Theme 2")
                .setSuggestedBy(moderatorUserEntity);

        themeSuggestionRepository.saveAllAndFlush(List.of(suggestion1, suggestion2));

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

    @Test
    void viewSuggestThemes_ShouldRedirectToLogin_WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/moderator/suggest-themes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/users/login"));
    }

    @Test
    void suggestTheme_ShouldRedirectToSuccess_WhenSuggestionIsSuccessful() throws Exception {
        String themeName = "New Unique Theme";
        assertFalse(themeSuggestionRepository.existsByName(themeName.toLowerCase()));
        assertFalse(categoryRepository.existsByName(themeName.toLowerCase()));

        mockMvc.perform(post("/moderator/suggest-theme")
                        .param("theme", themeName)
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/suggest-themes?success=Theme suggested successfully."));

        Optional<ThemeSuggestionEntity> savedSuggestion = themeSuggestionRepository.findByName(themeName.toLowerCase());
        assertTrue(savedSuggestion.isPresent());
        assertEquals(moderatorDetails.getId(), savedSuggestion.get().getSuggestedBy().getId());
    }

    @Test
    void suggestTheme_ShouldRedirectToError_WhenSuggestionAlreadyExist() throws Exception {
        String existingThemeName = "existingName";

        ThemeSuggestionEntity existingTheme = new ThemeSuggestionEntity()
                .setName(existingThemeName.toLowerCase())
                .setSuggestedBy(moderatorUserEntity);

        themeSuggestionRepository.saveAndFlush(existingTheme);

        assertTrue(themeSuggestionRepository.existsByName(existingThemeName.toLowerCase()));

        mockMvc.perform(post("/moderator/suggest-theme")
                        .param("theme", existingThemeName)
                        .with(authentication(moderatorAuthenticationToken))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/moderator/suggest-themes?error=Failed to suggest theme. Please try to suggest a different theme."));
    }

    @AfterEach
    void cleanUp() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
        themeSuggestionRepository.deleteAll();
    }

}
