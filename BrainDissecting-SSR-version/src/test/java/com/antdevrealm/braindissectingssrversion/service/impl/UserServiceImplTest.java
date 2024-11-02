package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ArticleRepository mockArticleRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private BrDissectingUserDetailService mockBrDissectingUserDetailService;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private ModelMapper mockModelMapper;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    private UserServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new UserServiceImpl(mockUserRepository, mockRoleRepository,
                mockPasswordEncoder, mockBrDissectingUserDetailService,
                mockArticleRepository, mockModelMapper);
    }

    @Test
    void register_ShouldReturnTrue() {
        RegistrationDTO registrationDTO = new RegistrationDTO().setUsername("testUsername")
                .setEmail("testEmail")
                .setPassword("testPassword")
                .setConfirmPassword("testPassword")
                .setFirstName("testFirstname")
                .setLastName("testLastname");

        when(mockPasswordEncoder.encode(registrationDTO.getPassword()))
                .thenReturn(registrationDTO.getPassword() + registrationDTO.getPassword());

        when(mockUserRepository.findByUsernameOrEmail(registrationDTO.getUsername(), registrationDTO.getEmail()))
                .thenReturn(Optional.empty());

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRole(UserRole.USER);

        when(mockRoleRepository.findByRole(UserRole.USER)).thenReturn(Optional.of(userRole));

        boolean result = toTest.register(registrationDTO);

        Mockito.verify(mockUserRepository).save(userEntityCaptor.capture());

        UserEntity savedEntity = userEntityCaptor.getValue();


        Assertions.assertEquals(registrationDTO.getUsername(), savedEntity.getUsername());
        Assertions.assertEquals(registrationDTO.getEmail(), savedEntity.getEmail());
        Assertions.assertEquals(registrationDTO.getPassword() + registrationDTO.getPassword(), savedEntity.getPassword());
        Assertions.assertEquals(registrationDTO.getFirstName(), savedEntity.getFirstName());
        Assertions.assertEquals(registrationDTO.getLastName(), savedEntity.getLastName());

        Assertions.assertTrue(result);
    }



}
