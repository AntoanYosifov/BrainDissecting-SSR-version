package com.antdevrealm.braindissectingssrversion.service.impl;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    private final long USER_ID = 1L;

    private final String USERNAME = "testUser";

    @Mock
    private  UserService mockUserService;
    @Mock
    private  UserRepository mockUserRepository;
    @Mock
    private  RoleRepository mockRoleRepository;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    private AdminServiceImpl toTest;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        toTest = new AdminServiceImpl(mockUserService, mockUserRepository, mockRoleRepository);

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setUsername(USERNAME);
        userEntity.setRoles(new ArrayList<>());
    }

    @Test
    void promoteToModerator_ShouldReturnTrue_WhenUserExistsAndRoleIsAdded() {
        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.promoteToModerator(USER_ID);

        Assertions.assertTrue(result);
        Assertions.assertTrue(userEntity.getRoles().contains(moderatorRole));
    }

    @Test
    void promoteToModerator_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

        boolean result = toTest.promoteToModerator(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void promoteToModerator_ShouldReturnFalse_WhenModeratorRoleDoesNotExist() {
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.empty());

        boolean result = toTest.promoteToModerator(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void promoteToModerator_ShouldReturnFalse_WhenUserAlreadyHasModeratorRole() {
        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);

        userEntity.setRoles(new ArrayList<>(List.of(moderatorRole)));

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.promoteToModerator(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void demoteFromModerator_ShouldReturnTrue_WhenUserExistsAndRoleIsRemoved() {
        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);
        userEntity.getRoles().add(moderatorRole);
        Assertions.assertTrue(userEntity.getRoles().contains(moderatorRole));

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.demoteFromModerator(USER_ID);

        Assertions.assertTrue(result);
        Assertions.assertFalse(userEntity.getRoles().contains(moderatorRole));
    }

    @Test
    void demoteFromModerator_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

        boolean result = toTest.demoteFromModerator(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void demoteFromModerator_ShouldReturnFalse_WhenModeratorRoleDoesNotExist() {
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.empty());

        boolean result = toTest.demoteFromModerator(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void demoteFromModerator_ShouldReturnFalse_WhenUserDoesNotHaveModeratorRole() {
        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.demoteFromModerator(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void banUser_ShouldReturnTrue_WhenUserExistsAndIsActive() {
        userEntity.setStatus(UserStatus.ACTIVE);

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.banUser(USER_ID);

        Mockito.verify(mockUserRepository).save(userEntityCaptor.capture());

        UserEntity savedEntity = userEntityCaptor.getValue();

        Assertions.assertTrue(result);
        Assertions.assertEquals(userEntity, savedEntity);
        Assertions.assertTrue(savedEntity.isBanned());
    }

    @Test
    void banUser_ShouldReturnFalse_WhenUserIsAlreadyBanned() {
        userEntity.setStatus(UserStatus.BANNED);

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.banUser(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void banUser_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

        boolean result = toTest.banUser(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void removeBan_ShouldReturnTrue_WhenUserExistsAndIsBanned() {
        userEntity.setStatus(UserStatus.BANNED);

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.removeBan(USER_ID);

        Mockito.verify(mockUserRepository).save(userEntityCaptor.capture());

        UserEntity savedEntity = userEntityCaptor.getValue();

        Assertions.assertTrue(result);
        Assertions.assertEquals(userEntity, savedEntity);
        Assertions.assertFalse(savedEntity.isBanned());
    }

    @Test
    void removeBan_ShouldReturnFalse_WhenUserIsActive() {
        userEntity.setStatus(UserStatus.ACTIVE);

        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.removeBan(USER_ID);

        Assertions.assertFalse(result);
    }

    @Test
    void removeBan_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

        boolean result = toTest.removeBan(USER_ID);

        Assertions.assertFalse(result);
    }

}
