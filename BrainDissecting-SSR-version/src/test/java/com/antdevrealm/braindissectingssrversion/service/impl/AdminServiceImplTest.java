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

    @Mock
    private  UserService mockUserService;
    @Mock
    private  UserRepository mockUserRepository;
    @Mock
    private  RoleRepository mockRoleRepository;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    private AdminServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new AdminServiceImpl(mockUserService, mockUserRepository, mockRoleRepository);
    }

    @Test
    void promoteToModerator_ShouldReturnTrue_WhenUserExistsAndRoleIsAdded() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setRoles(new ArrayList<>());

        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.promoteToModerator(userId);

        Assertions.assertTrue(result);
        Assertions.assertTrue(userEntity.getRoles().contains(moderatorRole));
    }

    @Test
    void promoteToModerator_ShouldReturnFalse_WhenUserDoesNotExist() {
        long userId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = toTest.promoteToModerator(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void promoteToModerator_ShouldReturnFalse_WhenModeratorRoleDoesNotExist() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setRoles(new ArrayList<>());

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.empty());

        boolean result = toTest.promoteToModerator(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void promoteToModerator_ShouldReturnFalse_WhenUserAlreadyHasModeratorRole() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");

        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);

        userEntity.setRoles(new ArrayList<>(List.of(moderatorRole)));

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.promoteToModerator(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void demoteFromModerator_ShouldReturnTrue_WhenUserExistsAndRoleIsRemoved() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setRoles(new ArrayList<>());

        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);
        userEntity.getRoles().add(moderatorRole);
        Assertions.assertTrue(userEntity.getRoles().contains(moderatorRole));

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.demoteFromModerator(userId);

        Assertions.assertTrue(result);
        Assertions.assertFalse(userEntity.getRoles().contains(moderatorRole));
    }

    @Test
    void demoteFromModerator_ShouldReturnFalse_WhenUserDoesNotExist() {
        long userId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = toTest.demoteFromModerator(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void demoteFromModerator_ShouldReturnFalse_WhenModeratorRoleDoesNotExist() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setRoles(new ArrayList<>());

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.empty());

        boolean result = toTest.demoteFromModerator(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void demoteFromModerator_ShouldReturnFalse_WhenUserDoesNotHaveModeratorRole() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");

        UserRoleEntity moderatorRole = new UserRoleEntity();
        moderatorRole.setRole(UserRole.MODERATOR);

        userEntity.setRoles(new ArrayList<>());

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockRoleRepository.findByRole(UserRole.MODERATOR)).thenReturn(Optional.of(moderatorRole));

        boolean result = toTest.demoteFromModerator(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void banUser_ShouldReturnTrue_WhenUserExistsAndIsActive() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("'testUser'");
        userEntity.setStatus(UserStatus.ACTIVE);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.banUser(userId);

        Mockito.verify(mockUserRepository).save(userEntityCaptor.capture());

        UserEntity savedEntity = userEntityCaptor.getValue();

        Assertions.assertTrue(result);
        Assertions.assertEquals(userEntity, savedEntity);
        Assertions.assertTrue(savedEntity.isBanned());
    }

    @Test
    void banUser_ShouldReturnFalse_WhenUserIsAlreadyBanned() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("'testUser'");
        userEntity.setStatus(UserStatus.BANNED);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.banUser(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void banUser_ShouldReturnFalse_WhenUserDoesNotExist() {
        long userId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = toTest.banUser(userId);

        Assertions.assertFalse(result);
    }

    @Test
    void removeBan_ShouldReturnTrue_WhenUserExistsAndIsBanned() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("'testUser'");
        userEntity.setStatus(UserStatus.BANNED);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.removeBan(userId);

        Mockito.verify(mockUserRepository).save(userEntityCaptor.capture());

        UserEntity savedEntity = userEntityCaptor.getValue();

        Assertions.assertTrue(result);
        Assertions.assertEquals(userEntity, savedEntity);
        Assertions.assertFalse(savedEntity.isBanned());
    }

    @Test
    void removeBan_ShouldReturnFalse_WhenUserIsActive() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("'testUser'");
        userEntity.setStatus(UserStatus.ACTIVE);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        boolean result = toTest.removeBan(userId);

        Assertions.assertFalse(result);
    }


}
