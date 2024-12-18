package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.user.DisplayUserInfoDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;

import java.util.List;

public interface UserService {

    boolean register(RegistrationDTO registrationDTO);

    void update(long loggedUserId, UpdateDTO updateDTO);

    void addArticleToFavourites(Long articleId, Long userId);

    void removeFromFavourites(Long articleId, Long userId);

    List<Long> getFavouriteArticlesIds(Long userId);

    List<DisplayUserInfoDTO> getAllUsers();

    void reloadUserDetails(String username);


}
