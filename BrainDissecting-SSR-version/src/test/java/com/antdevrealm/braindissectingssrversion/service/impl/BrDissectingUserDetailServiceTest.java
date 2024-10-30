package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class BrDissectingUserDetailServiceTest {

    private final String TEST_USERNAME = "testUsername";

    private BrDissectingUserDetailService toTest;

    private UserRepository mockUserRepository;

    @BeforeEach
    void setUp() {
        mockUserRepository = Mockito.mock(UserRepository.class);

        toTest = new BrDissectingUserDetailService(mockUserRepository);
    }

    @Test
    void testLoadUserByUsername_UserFound() {

        UserEntity testUser = new UserEntity()
                .setUsername("testUsername")
                .setEmail("testEmail")
                .setPassword("testPassword")
                .setFirstName("testFirstname")
                .setLastName("testLastname")
                .setStatus(UserStatus.ACTIVE)
                .setRoles(List.of(
                        new UserRoleEntity().setRole(UserRole.ADMIN),
                        new UserRoleEntity().setRole(UserRole.USER)
                ));

        when(mockUserRepository.findByUsername(TEST_USERNAME))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = toTest.loadUserByUsername(TEST_USERNAME);

        Assertions.assertInstanceOf(BrDissectingUserDetails.class, userDetails);

        BrDissectingUserDetails brDissectingUserDetails = (BrDissectingUserDetails) userDetails;

        Assertions.assertEquals(TEST_USERNAME, userDetails.getUsername());
        Assertions.assertEquals(testUser.getPassword(), userDetails.getPassword());
        Assertions.assertEquals(testUser.getEmail(), brDissectingUserDetails.getEmail());
        Assertions.assertEquals(testUser.getFirstName(), brDissectingUserDetails.getFirstName());
        Assertions.assertEquals(testUser.getLastName(), brDissectingUserDetails.getLastName());
        Assertions.assertEquals(testUser.getFirstName() + " " + testUser.getLastName(), brDissectingUserDetails.getFullName());
        Assertions.assertFalse(brDissectingUserDetails.isBanned());

        List<String> expectedRoles = testUser.getRoles().stream().map(UserRoleEntity::getRole).map(r -> "ROLE_" + r).toList();
        List<String> actualRoles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        Assertions.assertEquals(expectedRoles, actualRoles);

    }

}
