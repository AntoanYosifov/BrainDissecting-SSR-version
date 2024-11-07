package com.antdevrealm.braindissectingssrversion.service.impl;


import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
}
