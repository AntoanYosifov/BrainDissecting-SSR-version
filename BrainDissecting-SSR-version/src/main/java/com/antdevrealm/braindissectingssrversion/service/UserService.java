package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.user.LoginDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;

public interface UserService {
    boolean register(RegistrationDTO registrationDTO);
    boolean login(LoginDTO loginDTO);
}
