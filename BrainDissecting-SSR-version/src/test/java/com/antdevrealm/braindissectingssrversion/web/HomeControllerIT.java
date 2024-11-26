package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleServiceImpl articleService;

    private UserEntity loggedUserEntity;

    private BrDissectingUserDetails userDetails;

    private UsernamePasswordAuthenticationToken authenticationToken;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        loggedUserEntity = new UserEntity()
                .setUsername("moderatorUser")
                .setEmail("moderator@example.com")
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
        authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void shouldReturnHomeWithWelcomeMessageAndFavourites_WhenUserIsAuthenticated() throws Exception {
        mockMvc.perform(get("/").with(authentication(authenticationToken)))
                .andExpect(model().attributeExists("welcomeMessage"))
                .andExpect(model().attribute("welcomeMessage", "Logged User"))
                .andExpect(model().attributeExists("favourites"))
                .andExpect(view().name("home"));
    }

    @Test
    void shouldReturnIndex_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(model().attribute("welcomeMessage", "Anonymous"))
                .andExpect(view().name("index"));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

}
