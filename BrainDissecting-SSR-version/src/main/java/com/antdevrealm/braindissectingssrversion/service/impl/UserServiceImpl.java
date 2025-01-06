package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.*;
import com.antdevrealm.braindissectingssrversion.model.dto.user.DisplayUserInfoDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.BaseEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserRoleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserRole;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.RoleRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final static long DEMO_ADMIN_ID = 1L;

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final RoleRepository roleRepository;
    private final BrDissectingUserDetailService brDissectingUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, BrDissectingUserDetailService brDissectingUserDetailService,
                           ArticleRepository articleRepository,
                           ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.brDissectingUserDetailService = brDissectingUserDetailService;
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean register(RegistrationDTO data) {
        if (!passwordConfirmPasswordMatch(data)) {
            throw new PasswordConfirmPassMisMatchException();
        }

        UserEntity userEntity = mapToUser(data);

        if (userRepository.count() == 0) {
            Optional<UserRoleEntity> roleAmin = roleRepository.findByRole(UserRole.ADMIN);
            if(roleAmin.isEmpty()) {
                return false;
            }

            userEntity.getRoles().add(roleAmin.get());
        }

        if (usernameOrEmailExists(data.getUsername(), data.getEmail())) {
            throw new UsernameOrEmailException(data.getUsername(), data.getEmail());
        }

        Optional<UserRoleEntity> roleUser = roleRepository.findByRole(UserRole.USER);

        if(roleUser.isEmpty()) {
            return false;
        }

        userEntity.getRoles().add(roleUser.get());
        userRepository.save(userEntity);

        return true;
    }

    @Override
    public void update(long loggedUserId, UpdateDTO updateDTO) {
        if(loggedUserId == DEMO_ADMIN_ID) {
            throw new UnsupportedOperationException("The demo admin account can not be updated!");
        }

        UserEntity userEntity = userRepository.findById(loggedUserId).orElseThrow(() -> new UserNotFoundException(loggedUserId));

        if (usernameOrEmailExists(updateDTO.getNewUsername(), updateDTO.getNewEmail())) {
            throw new UsernameOrEmailException(updateDTO.getNewUsername(), updateDTO.getNewEmail());
        }

        if (!updateDTO.getNewUsername().equals(updateDTO.getConfirmUsername())) {
            throw new NewUsernameConfirmUsernameException(updateDTO.getNewUsername(), updateDTO.getConfirmUsername());
        }

        if (updateDTO.getNewEmail() != null && !updateDTO.getNewEmail().isEmpty()) {
            userEntity.setEmail(updateDTO.getNewEmail());
        }

        userEntity.setUsername(updateDTO.getNewUsername());

        userRepository.save(userEntity);

        reloadUserDetails(updateDTO.getNewUsername());
    }

    @Override
    @Transactional
    public void addArticleToFavourites(Long articleId, Long userId) {

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        ArticleEntity toFavouritesArt = articleRepository.findById(articleId).orElseThrow(() -> new ArticleNotFoundException(articleId));

        toFavouritesArt.setFavourite(true);

        userEntity.getFavourites().add(toFavouritesArt);

        userRepository.save(userEntity);

    }

    @Override
    @Transactional
    public void removeFromFavourites(Long articleId, Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow(() -> new ArticleNotFoundException(articleId));

        userEntity.getFavourites().remove(articleEntity);
        userRepository.save(userEntity);
    }

    @Override
    public List<Long> getFavouriteArticlesIds(Long userId) {
        Optional<UserEntity> optUser = userRepository.findById(userId);

        if (optUser.isEmpty()) {
            return new ArrayList<>();
        }

        UserEntity userEntity = optUser.get();

        return userEntity.getFavourites().stream().map(BaseEntity::getId).toList();
    }

    @Override
    public List<DisplayUserInfoDTO> getAllUsers() {

        if (userRepository.count() < 1) {
            return new ArrayList<>();
        }

        List<UserEntity> allUsers = userRepository.findAll();

        UserRoleEntity adminRole = roleRepository.findByRole(UserRole.ADMIN)
                .orElseThrow(() -> new RoleNotFoundException(UserRole.ADMIN));

        return allUsers.stream()
                .filter(u -> !u.getRoles().contains(adminRole)).map(this::mapToDisplayUserDto).toList();
    }


    private UserEntity mapToUser(RegistrationDTO data) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(data.getUsername())
                .setEmail(data.getEmail())
                .setPassword(passwordEncoder.encode(data.getPassword()))
                .setFirstName(data.getFirstName())
                .setLastName(data.getLastName())
                .setStatus(UserStatus.ACTIVE);

        return userEntity;
    }

    private DisplayUserInfoDTO mapToDisplayUserDto(UserEntity userEntity) {
        DisplayUserInfoDTO displayDTO = modelMapper.map(userEntity, DisplayUserInfoDTO.class);

        displayDTO.setRoles(userEntity.getRoles().stream().map(r -> r.getRole().toString()).toList());
        displayDTO.setStatus(userEntity.getStatus().name());

        return displayDTO;
    }

    private boolean usernameOrEmailExists(String username, String email) {
        Optional<UserEntity> byUsernameOrEmail = userRepository.findByUsernameOrEmail(username, email);
        return byUsernameOrEmail.isPresent();
    }

    private boolean passwordConfirmPasswordMatch(RegistrationDTO data) {
        return data.getPassword().equals(data.getConfirmPassword());
    }

    @Override
    public void reloadUserDetails(String username) {
        UserDetails updatedUserDetails = brDissectingUserDetailService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                updatedUserDetails.getPassword(),
                updatedUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
