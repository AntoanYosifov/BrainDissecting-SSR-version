package com.antdevrealm.braindissectingssrversion.service;

public interface AdminService {

    boolean promoteToModerator(Long userId);

    boolean demoteFromModerator(Long userId);

    void banUser(Long userId);

    void removeBan(Long userId);
}
