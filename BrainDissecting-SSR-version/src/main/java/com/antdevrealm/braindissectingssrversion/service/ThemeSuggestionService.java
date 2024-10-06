package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.entity.ThemeSuggestionEntity;

import java.util.List;

public interface ThemeSuggestionService {
    List<ThemeSuggestionEntity> getAll();
    List<ThemeSuggestionEntity> getAllModeratorSuggestions(Long moderatorId);
    boolean suggestTheme(String name, Long moderatorId);
    boolean approveTheme(Long id);
    boolean rejectTheme(Long id);
}
