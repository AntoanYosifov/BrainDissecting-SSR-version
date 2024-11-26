package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private UserEntity loggedUserEntity;

    private BrDissectingUserDetails userDetails;
    private UsernamePasswordAuthenticationToken authenticationToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        articleRepository.deleteAll();

        loggedUserEntity = new UserEntity()
                .setUsername("loggedUser")
                .setEmail("loggeduser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(loggedUserEntity);

        userDetails = new BrDissectingUserDetails(
                loggedUserEntity.getId(),
                loggedUserEntity.getEmail(),
                loggedUserEntity.getUsername(),
                loggedUserEntity.getPassword(),
                List.of(() -> "ROLE_USER"),
                "Logged",
                "User",
                false
        );

        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void add_ShouldRedirectWithSuccess_WhenCommentIsAdded() throws Exception {
        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        long articleId = articleRepository.saveAndFlush(articleEntity).getId();

        mockMvc.perform(post("/articles/" + articleId + "/comments")
                        .param("content", "Test content for comment on an article")
                        .with(csrf()).with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all?open=" + articleId + "#comment-1"));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        articleRepository.deleteAll();
    }
}
