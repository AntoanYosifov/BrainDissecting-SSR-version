package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.config.UserSession;
import com.antdevrealm.braindissectingssrversion.exception.RegistrationUsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.LoginDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
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
    private final UserSession userSession;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserSession userSession) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSession = userSession;
    }


    @Override
    public boolean register(RegistrationDTO data) {
        // TODO: Return custom errors
        if(usernameOrEmailExists(data)) {
            throw new RegistrationUsernameOrEmailException(data.getUsername(), data.getEmail());
        }

        if(!passwordConfirmPasswordMatch(data)) {
            return false;
        }

        User user = mapToUser(data);

        userRepository.save(user);

        return true;
    }

    @Override
    public boolean login(LoginDTO loginDTO) {
        Optional<User> byUsername = userRepository.findByUsername(loginDTO.getUsername());

        if (byUsername.isEmpty()) {
            return false;
        }

        User user = byUsername.get();

        // TODO: trow custom user password miss match error
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return false;
        }

        userSession.login(user.getId(), user.getUsername());

        return true;
    }


    private User mapToUser(RegistrationDTO data) {
        User user = new User();

        user.setUsername(data.getUsername())
                .setEmail(data.getEmail())
                .setPassword(passwordEncoder.encode(data.getPassword()));
        return user;
    }

    private boolean usernameOrEmailExists (RegistrationDTO data) {
        Optional<User> byUsernameOrEmail = userRepository.findByUsernameOrEmail(data.getUsername(), data.getEmail());
        return byUsernameOrEmail.isPresent();
    }

    private boolean passwordConfirmPasswordMatch(RegistrationDTO data) {
        return data.getPassword().equals(data.getConfirmPassword());
    }

}
