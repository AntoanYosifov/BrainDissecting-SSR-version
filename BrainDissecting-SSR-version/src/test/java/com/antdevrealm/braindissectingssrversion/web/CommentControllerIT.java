package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CommentEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.CommentRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private CommentRepository commentRepository;

    private UserEntity loggedUserEntity;

    private UsernamePasswordAuthenticationToken authenticationToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        articleRepository.deleteAll();
        commentRepository.deleteAll();

        loggedUserEntity = new UserEntity()
                .setUsername("loggedUser")
                .setEmail("loggeduser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(loggedUserEntity);

        BrDissectingUserDetails userDetails = new BrDissectingUserDetails(
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
                .andExpect(redirectedUrlPattern("/articles/all?open=" + articleId + "#comment-**"));
    }

    @Test
    void add_ShouldRedirectWithError_ArticleNotFound() throws Exception {
        mockMvc.perform(post("/articles/99999/comments")
                        .param("content", "Test content for comment on an article")
                        .with(csrf()).with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all?error=add_article_not_found"));
    }

    @Test
    void add_ShouldRedirectWithError_UserNotFound() throws Exception {
        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        long articleId = articleRepository.saveAndFlush(articleEntity).getId();

        userRepository.delete(loggedUserEntity);

        mockMvc.perform(post("/articles/" + articleId + "/comments")
                        .param("content", "Test content for comment on an article")
                        .with(csrf()).with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all?error=user_not_found"));
    }

    @Test
    void delete_ShouldRedirectWithSuccess_WhenCommentDeleted() throws Exception {
        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        long articleId = articleRepository.saveAndFlush(articleEntity).getId();

        CommentEntity commentToDelete = new CommentEntity()
                .setContent("Test content for comment to delete")
                .setUser(loggedUserEntity)
                .setArticle(articleEntity);

        long commentId = commentRepository.saveAndFlush(commentToDelete).getId();

        mockMvc.perform(delete("/articles/" + articleId + "/comments/delete/" + commentId)
                        .with(csrf()).with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all"));
    }


    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        articleRepository.deleteAll();
        commentRepository.deleteAll();
    }
}
