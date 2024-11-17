package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRedirectToLogin_WhenRegistrationIsSuccessful() throws Exception {

        roleRepository.save(new UserRoleEntity().setRole(UserRole.USER));

        mockMvc.perform(post("/users/register")
                .param("username", "testUsername")
                .param("email", "testemail@example.com")
                .param("password", "asdasd")
                .param("confirmPassword", "asdasd")
                        .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login"));

        Optional<UserEntity> optUserEntity = userRepository.findByUsername("testUsername");

        Assertions.assertTrue(optUserEntity.isPresent());

        UserEntity userEntity = optUserEntity.get();

        Assertions.assertEquals("testemail@example.com", userEntity.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("asdasd", userEntity.getPassword()));

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
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("registerData"));

        Assertions.assertEquals(1L, userRepository.count());
    }

    @Test
    void shouldRedirectToRegistration_WhenPasswordMissMatch() throws Exception {
        mockMvc.perform(post("/users/register")
                        .param("username", "existingUsername")
                        .param("email", "uniqueEmail@example.com")
                        .param("password", "validPassword")
                        .param("confirmPassword", "differentPassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("registerData"));

        Assertions.assertEquals(0L, userRepository.count());
    }

}
