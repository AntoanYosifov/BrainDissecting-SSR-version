package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.CategoryRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private UserEntity loggedUserAdminEntity;
    private UserEntity loggedUserNonAdminEntity;

    private UsernamePasswordAuthenticationToken authenticationAdminToken;
    private UsernamePasswordAuthenticationToken authenticationNonAdminToken;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        articleRepository.deleteAll();
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

        mockMvc.perform(patch("/admin/promote-moderator/" + userToPromoteId)
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

        mockMvc.perform(patch("/admin/promote-moderator/" + fakeUserId)
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

        mockMvc.perform(patch("/admin/promote-moderator/" + userToPromoteId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("roleAssignFailure"))
                .andExpect(flash().attribute("roleAssignFailure", "Failed to assign role!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));

        roleRepository.save(new UserRoleEntity().setRole(UserRole.MODERATOR));
    }

    @Test
    void demoteFromModerator_ShouldRedirectWithSuccess_WhenUserIsDemoted() throws Exception {
        Optional<UserRoleEntity> optModeratorRole = roleRepository.findByRole(UserRole.MODERATOR);

        Assertions.assertTrue(optModeratorRole.isPresent());

        UserRoleEntity moderatorRole = optModeratorRole.get();

        UserEntity userToDemote = new UserEntity()
                .setUsername("userToDemote")
                .setEmail("email@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE)
                .setRoles(new ArrayList<>(List.of(moderatorRole)));

        Assertions.assertTrue(userToDemote.getRoles().contains(moderatorRole));

        long userToDemoteId = userRepository.saveAndFlush(userToDemote).getId();

        mockMvc.perform(patch("/admin/demote-moderator/" + userToDemoteId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("removeRoleSuccess"))
                .andExpect(flash().attribute("removeRoleSuccess", "Role removed successfully!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));

        Optional<UserEntity> optDemotedUser = userRepository.findById(userToDemoteId);

        Assertions.assertTrue(optDemotedUser.isPresent());

        UserEntity demotedEntity = optDemotedUser.get();

        Assertions.assertFalse(demotedEntity.getRoles().contains(moderatorRole));
    }

    @Test
    void demoteFromModerator_ShouldRedirectWithFailure_WhenUserToDemoteNotFound() throws Exception {
        long fakeUserId = 99999;

        mockMvc.perform(patch("/admin/demote-moderator/" + fakeUserId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("removeRoleFailure"))
                .andExpect(flash().attribute("removeRoleFailure", "Failed to remove role!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));
    }

    @Test
    void demoteFromModerator_ShouldRedirectWithFailure_WhenRoleNotFound() throws Exception {
        Optional<UserRoleEntity> optModeratorRole = roleRepository.findByRole(UserRole.MODERATOR);

        Assertions.assertTrue(optModeratorRole.isPresent());

        UserRoleEntity moderatorRole = optModeratorRole.get();

        UserEntity userToDemote = new UserEntity()
                .setUsername("userToPromote")
                .setEmail("email@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE);

        long userToDemoteId = userRepository.saveAndFlush(userToDemote).getId();

        roleRepository.delete(moderatorRole);

        mockMvc.perform(patch("/admin/demote-moderator/" + userToDemoteId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("removeRoleFailure"))
                .andExpect(flash().attribute("removeRoleFailure", "Failed to remove role!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));

        roleRepository.save(new UserRoleEntity().setRole(UserRole.MODERATOR));
    }

    @Test
    void deleteArticle_ShouldDeleteExistingArticle_RedirectToAdminDeleteArticle() throws Exception {
        long articleToDeleteId = articleRepository.save(new ArticleEntity()
                .setTitle("articleToDeleteTitle")
                .setContent("articleToDeleteContent")
                .setStatus(Status.APPROVED)).getId();

        Assertions.assertTrue(articleRepository.existsById(articleToDeleteId));


        mockMvc.perform(delete("/admin/delete-article/" + articleToDeleteId)
                .with(csrf())
                .with(authentication(authenticationAdminToken)))
                .andExpect(redirectedUrl("/admin/delete-article"));

        Assertions.assertFalse(articleRepository.existsById(articleToDeleteId));
    }

    @Test
    void banUser_ShouldRedirectWithSuccess_WhenUserIsBanned() throws Exception {
        long userToBanId = userRepository.save(new UserEntity()
                .setUsername("userToBan")
                .setEmail("example@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE)).getId();

        mockMvc.perform(patch("/admin/ban-user/" + userToBanId)
                .with(csrf())
                .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("BanSuccess"))
                .andExpect(flash().attribute("BanSuccess", "BAN operation successful!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));

        Optional<UserEntity> optBannedUser = userRepository.findById(userToBanId);

        Assertions.assertTrue(optBannedUser.isPresent());

        Assertions.assertTrue(optBannedUser.get().isBanned());
    }

    @Test
    void banUser_ShouldRedirectWithFailure_WhenUserIsNotFound() throws Exception {
        long fakeUserId = 9999L;

        mockMvc.perform(patch("/admin/ban-user/" + fakeUserId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("BanFailure"))
                .andExpect(flash().attribute("BanFailure", "BAN operation has failed!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));
    }

    @Test
    void banUser_ShouldRedirectWithFailure_WhenUserIsAlreadyBanned() throws Exception {
        long alreadyBannedUserId = userRepository.save(new UserEntity()
                .setUsername("alreadyBanned")
                .setEmail("example@example.com")
                .setPassword("password")
                .setStatus(UserStatus.BANNED)).getId();

        mockMvc.perform(patch("/admin/ban-user/" + alreadyBannedUserId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("BanFailure"))
                .andExpect(flash().attribute("BanFailure", "BAN operation has failed!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));
    }

    @Test
    void removeBan_ShouldRedirectWithSuccess_WhenBanIsRemoved() throws Exception {
        long userToRemoveBanFromId = userRepository.save(new UserEntity()
                .setUsername("userToRemoveBan")
                .setEmail("example@example.com")
                .setPassword("password")
                .setStatus(UserStatus.BANNED)).getId();

        mockMvc.perform(patch("/admin/remove-ban/" + userToRemoveBanFromId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("removeBanSuccess"))
                .andExpect(flash().attribute("removeBanSuccess", "BAN removed successfully!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));

        Optional<UserEntity> unBannedUser = userRepository.findById(userToRemoveBanFromId);

        Assertions.assertTrue(unBannedUser.isPresent());
        Assertions.assertFalse(unBannedUser.get().isBanned());
    }

    @Test
    void removeBan_ShouldRedirectWithFailure_WhenUserIsNotFound() throws Exception {
        long fakeUserId = 9999L;

        mockMvc.perform(patch("/admin/remove-ban/" + fakeUserId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("removeBanFailure"))
                .andExpect(flash().attribute("removeBanFailure", "Failed to remove BAN!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));
    }

    @Test
    void removeBan_ShouldRedirectWithFailure_WhenUserIsNotBanned() throws Exception {
        long activeUserId = userRepository.save(new UserEntity()
                .setUsername("userToRemoveBan")
                .setEmail("example@example.com")
                .setPassword("password")
                .setStatus(UserStatus.ACTIVE)).getId();

        mockMvc.perform(patch("/admin/remove-ban/" + activeUserId)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(flash().attributeExists("removeBanFailure"))
                .andExpect(flash().attribute("removeBanFailure", "Failed to remove BAN!"))
                .andExpect(redirectedUrl("/admin/manage-roles"));
    }

    @Test
    void addTheme_ShouldRedirectWithSuccess_WhenThemeIsAdded() throws Exception {
        String themeToAdd = "testThemeForAddition";

        mockMvc.perform(post("/admin/add-theme")
                        .param("theme", themeToAdd)
                        .with(csrf())
                        .with(authentication(authenticationAdminToken)))
                .andExpect(redirectedUrl("/admin/manage-themes?success=Theme added!"));

        Assertions.assertTrue(categoryRepository.existsByName(themeToAdd));
    }


    @AfterEach
    void cleanUp() {
        categoryRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

}
