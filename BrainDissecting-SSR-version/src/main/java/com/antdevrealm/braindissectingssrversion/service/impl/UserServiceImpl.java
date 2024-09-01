package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.exception.RegistrationUsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public boolean register(RegistrationDTO data) {

        if (usernameOrEmailExists(data)) {
            throw new RegistrationUsernameOrEmailException(data.getUsername(), data.getEmail());
        }

        if (!passwordConfirmPasswordMatch(data)) {
            return false;
        }

        UserEntity userEntity = mapToUser(data);

        userRepository.save(userEntity);
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

    private boolean usernameOrEmailExists(RegistrationDTO data) {
        Optional<UserEntity> byUsernameOrEmail = userRepository.findByUsernameOrEmail(data.getUsername(), data.getEmail());
        return byUsernameOrEmail.isPresent();
    }

    private boolean passwordConfirmPasswordMatch(RegistrationDTO data) {
        return data.getPassword().equals(data.getConfirmPassword());
    }

}
