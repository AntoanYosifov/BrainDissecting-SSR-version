package com.antdevrealm.braindissectingssrversion.service;

public interface AdminService {

    void promoteToModerator(Long userId);

    void demoteFromModerator(Long userId);

    boolean banUser(Long userId);

    boolean removeBan(Long userId);
}
