package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;

public interface UserService {
    boolean register(RegistrationDTO registrationDTO);

    boolean update(long loggedUserId, UpdateDTO updateDTO);

}
