package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserEntity loggedUserAdminEntity;
    private UserEntity loggedUserNonAdminEntity;

    private UsernamePasswordAuthenticationToken authenticationAdminToken;
    private UsernamePasswordAuthenticationToken authenticationNonAdminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        loggedUserAdminEntity = new UserEntity()
                .setUsername("adminUser")
                .setEmail("adminuser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(loggedUserAdminEntity);

        BrDissectingUserDetails userAdminDetails = new BrDissectingUserDetails(
                loggedUserAdminEntity.getId(),
                loggedUserAdminEntity.getEmail(),
                loggedUserAdminEntity.getUsername(),
                loggedUserAdminEntity.getPassword(),
                List.of(() -> "ROLE_ADMIN"),
                "Admin",
                "User",
                false
        );

        authenticationAdminToken = new UsernamePasswordAuthenticationToken(
                userAdminDetails, null, userAdminDetails.getAuthorities());

        loggedUserNonAdminEntity = new UserEntity()
                .setUsername("nonAdminUser")
                .setEmail("nonadminuser@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        userRepository.saveAndFlush(loggedUserNonAdminEntity);

        BrDissectingUserDetails userNonAdminDetails = new BrDissectingUserDetails(
                loggedUserAdminEntity.getId(),
                loggedUserAdminEntity.getEmail(),
                loggedUserAdminEntity.getUsername(),
                loggedUserAdminEntity.getPassword(),
                List.of(() -> "ROLE_USER"),
                "NonAdmin",
                "User",
                false
        );

        authenticationNonAdminToken = new UsernamePasswordAuthenticationToken(
                userNonAdminDetails, null, userNonAdminDetails.getAuthorities());
    }

    @Test
    void viewAdminManage_ShouldReturn_ManageUsers_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/admin/manage-roles").with(authentication(authenticationAdminToken)))
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("manage-users"));
    }

    @Test
    void viewAdminManage_ShouldRedirectToAccessDenied_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/manage-roles").with(authentication(authenticationNonAdminToken)))
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void promoteToModerator_ShouldRedirectWithSuccess_WhenUserIsPromoted() throws Exception {
        UserEntity userToPromote = new UserEntity()
                .setUsername("userToPromote")
                .setEmail("email@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        long userToPromoteId = userRepository.saveAndFlush(userToPromote).getId();

        mockMvc.perform(post("/admin/promote-moderator/" + userToPromoteId)
                .with(csrf())
                .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("roleAssignSuccess"))
                .andExpect(flash().attribute("roleAssignSuccess", "Role assigned successfully!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));

        Optional<UserEntity> optPromotedUser = userRepository.findById(userToPromoteId);

        Assertions.assertTrue(optPromotedUser.isPresent());

        UserEntity moderatorEntity = optPromotedUser.get();

        Optional<UserRoleEntity> optModeratorRole = roleRepository.findByRole(UserRole.MODERATOR);

        Assertions.assertTrue(optModeratorRole.isPresent());

        UserRoleEntity moderatorRole = optModeratorRole.get();

        Assertions.assertTrue(moderatorEntity.getRoles().contains(moderatorRole));
    }

    @Test
    void promoteToModerator_ShouldRedirectWithFailure_WhenUserToPromoteNotFound() throws Exception {
        long fakeUserId = 99999;

        mockMvc.perform(post("/admin/promote-moderator/" + fakeUserId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("roleAssignFailure"))
                .andExpect(flash().attribute("roleAssignFailure", "Failed to assign role!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));
    }

    @Test
    void promoteToModerator_ShouldRedirectWithFailure_WhenRoleIsNotFound() throws Exception {
        UserEntity userToPromote = new UserEntity()
                .setUsername("userToPromote")
                .setEmail("email@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        long userToPromoteId = userRepository.saveAndFlush(userToPromote).getId();

        Optional<UserRoleEntity> optModeratorRole = roleRepository.findByRole(UserRole.MODERATOR);

        Assertions.assertTrue(optModeratorRole.isPresent());

        UserRoleEntity moderatorRole = optModeratorRole.get();

        roleRepository.delete(moderatorRole);

        mockMvc.perform(post("/admin/promote-moderator/" + userToPromoteId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("roleAssignFailure"))
                .andExpect(flash().attribute("roleAssignFailure", "Failed to assign role!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

}
