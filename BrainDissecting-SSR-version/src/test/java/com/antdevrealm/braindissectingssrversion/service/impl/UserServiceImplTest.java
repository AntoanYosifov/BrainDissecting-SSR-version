package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.NewUsernameConfirmUsernameException;
import com.antdevrealm.braindissectingssrversion.exception.UsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
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
    void register_ShouldThrowException_WhenUsernameOrEmailExists() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("testUsername")
                .setEmail("testEmail")
                .setPassword("testPassword")
                .setConfirmPassword("testPassword");

        when(mockUserRepository.findByUsernameOrEmail(registrationDTO.getUsername(), registrationDTO.getEmail()))
                .thenReturn(Optional.of(new UserEntity()));

        Assertions.assertThrows(UsernameOrEmailException.class, () -> toTest.register(registrationDTO));

    }

    @Test
    void register_ShouldReturnFalse_WhenPasswordConfirmationMismatch() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("testUsername")
                .setEmail("testEmail")
                .setPassword("testPassword")
                .setConfirmPassword("differentPassword");

        boolean result = toTest.register(registrationDTO);

        Assertions.assertFalse(result);
        Mockito.verify(mockUserRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void register_ShouldReturnFalse_WhenUserRoleNotFound() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("testUsername")
                .setEmail("testEmail")
                .setPassword("testPassword")
                .setConfirmPassword("testPassword");

        when(mockUserRepository.findByUsernameOrEmail(registrationDTO.getUsername(), registrationDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(mockRoleRepository.findByRole(UserRole.USER))
                .thenReturn(Optional.empty());

        boolean result = toTest.register(registrationDTO);

        Assertions.assertFalse(result);
        Mockito.verify(mockUserRepository, Mockito.never()).save(Mockito.any());
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

    @Test
    void update_ShouldReturnFalse_WhenUserNotFound() {
        long loggedUserId = 1L;

        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setNewUsername("newUserName");

        when(mockUserRepository.findById(loggedUserId)).thenReturn(Optional.empty());

        boolean result = toTest.update(loggedUserId, updateDTO);

        Assertions.assertFalse(result);
    }

    @Test
    void update_UsernameOrEmailExists_ShouldThrowException() {
        long loggedUserId = 1L;
        UpdateDTO updateDTO = new UpdateDTO().setNewUsername("existingUsername").setNewEmail("existingEmail");

        UserEntity existingUser = new UserEntity();
        when(mockUserRepository.findById(loggedUserId)).thenReturn(Optional.of(existingUser));
        when(mockUserRepository.findByUsernameOrEmail(updateDTO.getNewUsername(), updateDTO.getNewEmail()))
                .thenReturn(Optional.of(new UserEntity()));

        Assertions.assertThrows(UsernameOrEmailException.class, () -> toTest.update(loggedUserId, updateDTO));
    }

    @Test
    void update_NewUsernameAndConfirmUsernameMisMatch_ShouldThrowException() {
        long loggedUserId = 1L;
        UpdateDTO updateDTO = new UpdateDTO()
                .setNewUsername("newUsername")
                .setConfirmUsername("differentUsername");

        UserEntity userEntity = new UserEntity();
        when(mockUserRepository.findById(loggedUserId)).thenReturn(Optional.of(userEntity));

        Assertions.assertThrows(NewUsernameConfirmUsernameException.class, () -> toTest.update(loggedUserId, updateDTO));
    }

    @Test
    void update_SuccessfulUpdate_ShouldReturnTrue() {
        long loggedUserId = 1L;
        UpdateDTO updateDTO = new UpdateDTO()
                .setNewUsername("newUsername")
                .setConfirmUsername("newUsername")
                .setNewEmail("newEmail");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("oldUsername").setEmail("oldEmail");

        doReturn(Optional.of(userEntity)).when(mockUserRepository).findById(loggedUserId);
        doReturn(Optional.empty()).when(mockUserRepository).findByUsernameOrEmail(updateDTO.getNewUsername(), updateDTO.getNewEmail());
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);

        when(mockUserDetails.getPassword()).thenReturn("encodedPassword");
        when(mockUserDetails.getAuthorities()).thenReturn(List.of());
        when(mockBrDissectingUserDetailService.loadUserByUsername(updateDTO.getNewUsername())).thenReturn(mockUserDetails);

        boolean result = toTest.update(loggedUserId, updateDTO);

        Assertions.assertTrue(result);

        Mockito.verify(mockUserRepository).save(userEntityCaptor.capture());
        UserEntity savedUser = userEntityCaptor.getValue();

        Assertions.assertEquals(updateDTO.getNewUsername(), savedUser.getUsername());
        Assertions.assertEquals(updateDTO.getNewEmail(), savedUser.getEmail());
    }

    @Test
    void addArticleToFavourites_ShouldReturnTrue_WhenArticleAndUserExists() {
        long articleId = 1L;
        long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setFavourites(new ArrayList<>());

        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(articleId);
        articleEntity.setTitle("testArticle");

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockArticleRepository.findById(articleId)).thenReturn(Optional.of(articleEntity));

        boolean result = toTest.addArticleToFavourites(articleId, userId);

        Assertions.assertTrue(result);
        Assertions.assertTrue(userEntity.getFavourites().contains(articleEntity));
        Assertions.assertTrue(articleEntity.isFavourite());
    }

}
