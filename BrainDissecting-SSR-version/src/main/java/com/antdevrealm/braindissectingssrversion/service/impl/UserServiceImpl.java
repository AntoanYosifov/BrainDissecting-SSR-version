package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.NewUsernameConfirmUsernameException;
import com.antdevrealm.braindissectingssrversion.exception.UsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.DisplayUserInfoDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.BaseEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.UserStatus;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BrDissectingUserDetailService brDissectingUserDetailService;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;


    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, BrDissectingUserDetailService brDissectingUserDetailService, ArticleRepository articleRepository, ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.brDissectingUserDetailService = brDissectingUserDetailService;
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean register(RegistrationDTO data) {

        if (usernameOrEmailExists(data.getUsername(), data.getEmail())) {
            throw new UsernameOrEmailException(data.getUsername(), data.getEmail());
        }

        if (!passwordConfirmPasswordMatch(data)) {
            return false;
        }

        UserEntity userEntity = mapToUser(data);
        userRepository.save(userEntity);

        return true;
    }

    @Override
    public boolean update(long loggedUserId, UpdateDTO updateDTO) {
        Optional<UserEntity> byId = userRepository.findById(loggedUserId);

        if (byId.isEmpty()) {
            return false;
        }

        if (usernameOrEmailExists(updateDTO.getNewUsername(), updateDTO.getNewEmail())) {
            throw new UsernameOrEmailException(updateDTO.getNewUsername(), updateDTO.getNewEmail());
        }

        if (!updateDTO.getNewUsername().equals(updateDTO.getConfirmUsername())) {
            throw new NewUsernameConfirmUsernameException(updateDTO.getNewUsername(), updateDTO.getConfirmUsername());
        }

        UserEntity userEntity = byId.get();

        if (!updateDTO.getNewEmail().isEmpty()) {
            userEntity.setEmail(updateDTO.getNewEmail());
        }

        userEntity.setUsername(updateDTO.getNewUsername());

        userRepository.save(userEntity);

        reloadUserDetails(updateDTO.getNewUsername());

        return true;
    }

    @Override
    @Transactional
    public boolean addArticleToFavourites(Long articleId, Long userId) {

        Optional<UserEntity> optUser = userRepository.findById(userId);

        if (optUser.isEmpty()) {
            return false;
        }

        Optional<ArticleEntity> optArt = articleRepository.findById(articleId);

        if (optArt.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();
        ArticleEntity toFavouritesArt = optArt.get();

        userEntity.getFavourites().add(toFavouritesArt);

        userRepository.save(userEntity);

        return true;
    }

    @Override
    @Transactional
    public boolean removeFromFavourites(Long articleId, Long userId) {

        Optional<UserEntity> optUser = userRepository.findById(userId);

        if (optUser.isEmpty()) {
            return false;
        }

        Optional<ArticleEntity> optArt = articleRepository.findById(articleId);

        if (optArt.isEmpty()) {
            return false;
        }

        UserEntity userEntity = optUser.get();
        ArticleEntity articleEntity = optArt.get();

        userEntity.getFavourites().remove(articleEntity);

        userRepository.save(userEntity);

        return true;
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
        List<UserEntity> allUsers = userRepository.findAll();

        if (allUsers.isEmpty()) {
            return new ArrayList<>();
        }

        return allUsers.stream().map(this::mapToDisplayUserDto).toList();
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
