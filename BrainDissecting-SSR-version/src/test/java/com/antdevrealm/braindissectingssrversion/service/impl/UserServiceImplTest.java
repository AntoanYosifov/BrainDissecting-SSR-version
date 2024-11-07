package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.NewUsernameConfirmUsernameException;
import com.antdevrealm.braindissectingssrversion.exception.UsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.DisplayUserInfoDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
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

    @Test
    void addArticleToFavourites_ShouldReturnFalse_WhenUserNotFound() {
        Long userId = 1L;
        Long articleId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = toTest.addArticleToFavourites(articleId, userId);
        Assertions.assertFalse(result);
    }

    @Test
    void addArticleToFavourites_ShouldReturnFalse_WhenArticleNotFound() {
        Long userId = 1L;
        Long articleId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockArticleRepository.findById(articleId)).thenReturn(Optional.empty());

        boolean result = toTest.addArticleToFavourites(articleId, userId);
        Assertions.assertFalse(result);
    }

    @Test
    void removeFromFavourites_ShouldReturnTrue_WhenArticleAndUserExists() {
        long articleId = 1L;
        long userId = 1L;

        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(articleId);
        articleEntity.setTitle("testArticle");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setFavourites(new ArrayList<>());
        userEntity.getFavourites().add(articleEntity);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockArticleRepository.findById(articleId)).thenReturn(Optional.of(articleEntity));

        boolean result = toTest.removeFromFavourites(articleId, userId);

        Assertions.assertTrue(result);
        Assertions.assertFalse(userEntity.getFavourites().contains(articleEntity));
    }

    @Test
    void removeFromFavourites_ShouldReturnFalse_WhenUserNotFound() {
        long userId = 1L;
        long articleId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = toTest.removeFromFavourites(userId, articleId);
        Assertions.assertFalse(result);
    }

    @Test
    void removeFromFavourites_ShouldReturnFalse_WhenArticleNotFound() {
        long userId = 1L;
        long articleId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUsername");

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockArticleRepository.findById(articleId)).thenReturn(Optional.empty());

        boolean result = toTest.removeFromFavourites(userId, articleId);
        Assertions.assertFalse(result);
    }

    @Test
    void getFavouriteArticlesIds_ShouldReturnListOfFavouriteArticleIds() {
        long userId = 1L;
        long articleId1 = 100L;
        long articleId2 = 101L;

        ArticleEntity articleEntity1 = new ArticleEntity();
        articleEntity1.setId(articleId1);

        ArticleEntity articleEntity2 = new ArticleEntity();
        articleEntity2.setId(articleId2);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFavourites(List.of(articleEntity1, articleEntity2));

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        List<Long> favouriteArticlesIds = toTest.getFavouriteArticlesIds(userId);

        Assertions.assertEquals(2, favouriteArticlesIds.size());
        Assertions.assertTrue(favouriteArticlesIds.contains(articleId1));
        Assertions.assertTrue(favouriteArticlesIds.contains(articleId2));
    }

    @Test
    void getFavouriteArticlesIds_ShouldReturnEmptyList_WhenUserDoesNotExist() {
        long userId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        List<Long> favouriteArticleIds = toTest.getFavouriteArticlesIds(userId);

        Assertions.assertTrue(favouriteArticleIds.isEmpty());
    }

    @Test
    void getFavouriteArticlesIds_ShouldReturnEmptyList_WhenUserHasNoFavourites() {
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFavourites(new ArrayList<>()); // No favorites

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        List<Long> favouriteArticleIds = toTest.getFavouriteArticlesIds(userId);

        Assertions.assertTrue(favouriteArticleIds.isEmpty());
    }

    @Test
    void getAllUsers_ShouldReturnListOfDisplayUserInfoDTO_WhenUsersExist() {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId(1L);
        userEntity1.setUsername("user1");
        userEntity1.setStatus(UserStatus.ACTIVE);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId(2L);
        userEntity2.setUsername("user2");
        userEntity2.setStatus(UserStatus.ACTIVE);

        List<UserEntity> userEntities = List.of(userEntity1, userEntity2);

        DisplayUserInfoDTO displayUserInfoDTO1 = new DisplayUserInfoDTO();
        displayUserInfoDTO1.setId(1L);
        displayUserInfoDTO1.setUsername("user1");

        DisplayUserInfoDTO displayUserInfoDTO2 = new DisplayUserInfoDTO();
        displayUserInfoDTO1.setId(2L);
        displayUserInfoDTO1.setUsername("user2");

        when(mockUserRepository.count()).thenReturn((long) userEntities.size());
        when(mockUserRepository.findAll()).thenReturn(userEntities);
        when(mockModelMapper.map(userEntity1, DisplayUserInfoDTO.class)).thenReturn(displayUserInfoDTO1);
        when(mockModelMapper.map(userEntity2, DisplayUserInfoDTO.class)).thenReturn(displayUserInfoDTO2);

        List<DisplayUserInfoDTO> result = toTest.getAllUsers();

        Assertions.assertEquals(userEntities.size(), result.size());
        Assertions.assertEquals(displayUserInfoDTO1, result.get(0));
        Assertions.assertEquals(displayUserInfoDTO2, result.get(1));
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(mockUserRepository.count()).thenReturn(0L);

        List<DisplayUserInfoDTO> result = toTest.getAllUsers();

        Assertions.assertTrue(result.isEmpty());
    }

}
