package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.RegistrationUsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BrDissectingUserDetailService brDissectingUserDetailService;



    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, BrDissectingUserDetailService brDissectingUserDetailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.brDissectingUserDetailService = brDissectingUserDetailService;
    }


    @Override
    public boolean register(RegistrationDTO data) {

        if (usernameOrEmailExists(data.getUsername(), data.getEmail())) {
            throw new RegistrationUsernameOrEmailException(data.getUsername(), data.getEmail());
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

        if(byId.isEmpty()) {
            return false;
        }

        if(usernameOrEmailExists(updateDTO.getNewUsername(), updateDTO.getNewEmail())) {
            return false;
        }

        UserEntity userEntity = byId.get();

        userEntity.setUsername(updateDTO.getNewUsername())
                .setEmail(updateDTO.getNewEmail());

        userRepository.save(userEntity);

        UserDetails updatedUserDetails = brDissectingUserDetailService.loadUserByUsername(updateDTO.getNewUsername());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(updatedUserDetails,
                updatedUserDetails.getPassword(),
                updatedUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return true;
    }

    private UserEntity mapToUser(RegistrationDTO data) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(data.getUsername())
                .setEmail(data.getEmail())
                .setPassword(passwordEncoder.encode(data.getPassword()))
                .setFirstName(data.getFirstName())
                .setLastName(data.getLastName());

        return userEntity;
    }

    private boolean usernameOrEmailExists(String username, String email) {
        Optional<UserEntity> byUsernameOrEmail = userRepository.findByUsernameOrEmail(username, email);
        return byUsernameOrEmail.isPresent();
    }

    private boolean passwordConfirmPasswordMatch(RegistrationDTO data) {
        return data.getPassword().equals(data.getConfirmPassword());
    }

}
