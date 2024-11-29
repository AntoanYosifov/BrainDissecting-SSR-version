package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    private UserEntity loggedUserEntity;

    private UsernamePasswordAuthenticationToken authenticationToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        loggedUserEntity = new UserEntity()
                .setUsername("adminUser")
                .setEmail("adminuser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(loggedUserEntity);

        BrDissectingUserDetails userDetails = new BrDissectingUserDetails(
                loggedUserEntity.getId(),
                loggedUserEntity.getEmail(),
                loggedUserEntity.getUsername(),
                loggedUserEntity.getPassword(),
                List.of(() -> "ROLE_ADMIN"),
                "Admin",
                "User",
                false
        );

        authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void viewAdminManage_ShouldReturn_ManageUsers_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/admin/manage-roles").with(authentication(authenticationToken)))
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("manage-users"));
    }
}
