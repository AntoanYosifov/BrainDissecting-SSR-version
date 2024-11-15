package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRedirectToLogin_WhenRegistrationIsSuccessful() throws Exception {
        mockMvc.perform(post("/users/register")
                .param("username", "testUsername")
                .param("email", "testEmail@example.com")
                .param("password", "asdasd")
                .param("confirmPassword", "asdasd")
                        .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login"));
    }

    @Test
    void shouldRedirectToRegistration_WhenUsernameAlreadyExists() throws Exception {
        UserEntity existingUser = new UserEntity()
                .setUsername("existingUsername")
                .setEmail("existingEmail@example.com")
                .setPassword("encodedPassword")
                .setStatus(UserStatus.ACTIVE);

        userRepository.save(existingUser);

        mockMvc.perform(post("/users/register")
                        .param("username", "existingUsername")
                        .param("email", "uniqueEmail@example.com")
                        .param("password", "asdasd")
                        .param("confirmPassword", "asdasd")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"));

        Assertions.assertEquals(1L, userRepository.count());
    }
}
