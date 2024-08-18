package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.dto.user.UserRegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.entity.User;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public boolean register(UserRegistrationDTO data) {
        // Return custom errors
        if(usernameOrEmailExists(data)) {
            return false;
        }

        if(!passwordConfirmPasswordMatch(data)) {
            return false;
        }

        User user = mapToUser(data);

        userRepository.save(user);

        return true;
    }

    private User mapToUser(UserRegistrationDTO data) {
        User user = new User();

        user.setUsername(data.getUsername())
                .setEmail(data.getEmail())
                .setPassword(passwordEncoder.encode(data.getPassword()));
        return user;
    }

    private boolean usernameOrEmailExists (UserRegistrationDTO data) {
        Optional<User> byUsernameOrEmail = userRepository.findByUsernameOrEmail(data.getUsername(), data.getEmail());
        return byUsernameOrEmail.isPresent();
    }

    private boolean passwordConfirmPasswordMatch(UserRegistrationDTO data) {
        return data.getPassword().equals(data.getConfirmPassword());
    }
}
