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

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
        commentRepository.deleteAll();
        userRepository.deleteAll();
        articleRepository.deleteAll();

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

    @Test
    void delete_ShouldRedirectWithError_ArticleNotFound() throws Exception {
        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        articleRepository.saveAndFlush(articleEntity);

        CommentEntity commentToDelete = new CommentEntity()
                .setContent("Test content for comment to delete")
                .setUser(loggedUserEntity)
                .setArticle(articleEntity);

        long nonExistArticleId = 9999999;

        long commentId = commentRepository.saveAndFlush(commentToDelete).getId();

        mockMvc.perform(delete("/articles/" + nonExistArticleId + "/comments/delete/" + commentId)
                        .with(csrf()).with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all?error=delete_article_not_found"));
    }

    @Test
    void delete_ShouldRedirectWithError_UserNotFound() throws Exception {
        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        long articleId = articleRepository.saveAndFlush(articleEntity).getId();

        long commentId = 1L;

        userRepository.delete(loggedUserEntity);

        mockMvc.perform(delete("/articles/" + articleId + "/comments/delete/" + commentId)
                        .with(csrf()).with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all?error=user_not_found"));
    }

    @Test
    void delete_ShouldRedirectWithError_CommentNotFound() throws Exception {
        ArticleEntity articleEntity = new ArticleEntity()
                .setTitle("testTitle")
                .setContent("testContent")
                .setStatus(Status.APPROVED);

        long articleId = articleRepository.saveAndFlush(articleEntity).getId();

        CommentEntity commentToDelete = new CommentEntity()
                .setContent("Test content for comment to delete")
                .setUser(loggedUserEntity)
                .setArticle(articleEntity);

        commentRepository.saveAndFlush(commentToDelete);

        long nonExistCommentId = 9999999;

        mockMvc.perform(delete("/articles/" + articleId + "/comments/delete/" + nonExistCommentId)
                        .with(csrf()).with(authentication(authenticationToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/all?error=comment_not_found"));
    }


    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        articleRepository.deleteAll();
    }
}
