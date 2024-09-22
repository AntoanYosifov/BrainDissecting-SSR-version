package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.dto.user.DisplayUserInfoDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;

import java.util.List;

public interface UserService {

    boolean register(RegistrationDTO registrationDTO);

    boolean update(long loggedUserId, UpdateDTO updateDTO);

    boolean addArticleToFavourites(Long articleId, Long userId);

    boolean removeFromFavourites(Long articleId, Long userId);

    List<Long> getFavouriteArticlesIds(Long userId);

    List<DisplayUserInfoDTO> getAllUsers();

    void reloadUserDetails(String username);


}
