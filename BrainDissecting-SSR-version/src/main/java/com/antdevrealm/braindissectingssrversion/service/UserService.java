package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.user.UserRegistrationDTO;

public interface UserService {
    boolean register(UserRegistrationDTO userRegistrationDTO);
}
