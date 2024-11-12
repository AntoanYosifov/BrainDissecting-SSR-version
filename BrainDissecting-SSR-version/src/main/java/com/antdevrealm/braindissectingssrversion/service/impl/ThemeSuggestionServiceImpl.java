package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.ThemeSuggestionEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.CategoryRepository;
import com.antdevrealm.braindissectingssrversion.repository.ThemeSuggestionRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import com.antdevrealm.braindissectingssrversion.service.ThemeSuggestionService;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ThemeSuggestionServiceImpl implements ThemeSuggestionService {

    private final ThemeSuggestionRepository themeSuggestionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ThemeSuggestionServiceImpl(ThemeSuggestionRepository themeSuggestionRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.themeSuggestionRepository = themeSuggestionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<ThemeSuggestionEntity> getAll() {
        return themeSuggestionRepository.findAll();
    }

    @Override
    public List<ThemeSuggestionEntity> getAllModeratorSuggestions(Long moderatorId) {
        return themeSuggestionRepository.findAllBySuggestedById(moderatorId);
    }

    @Override
    public boolean suggestTheme(String name, Long moderatorId) {
        Optional<UserEntity> optUser = userRepository.findById(moderatorId);

        if(optUser.isEmpty() || themeSuggestionRepository.existsByName(name.toLowerCase())) {
            return false;
        }

        UserEntity moderator = optUser.get();

        ThemeSuggestionEntity themeSuggestionEntity = new ThemeSuggestionEntity();

        themeSuggestionEntity.setName(name.toLowerCase())
                .setSuggestedBy(moderator);

        if (categoryRepository.existsByName(name.toLowerCase())) {
            return false;
        }

        themeSuggestionRepository.save(themeSuggestionEntity);
        return true;
    }

    @Override
    @Transactional
    @Modifying
    public boolean approveTheme(Long id) {
        Optional<ThemeSuggestionEntity> byId = themeSuggestionRepository.findById(id);

        if(byId.isEmpty()) {
            return false;
        }

        ThemeSuggestionEntity themeSuggestion = byId.get();

        if(categoryRepository.existsByName(themeSuggestion.getName().toLowerCase())) {
            return false;
        }

        CategoryEntity categoryEntity = new CategoryEntity(themeSuggestion.getName());

        themeSuggestionRepository.delete(themeSuggestion);
        categoryRepository.save(categoryEntity);
        return true;
    }

    @Override
    @Transactional
    @Modifying
    public boolean rejectTheme(Long id) {
        Optional<ThemeSuggestionEntity> byId = themeSuggestionRepository.findById(id);

        if(byId.isEmpty()) {
            return false;
        }

        ThemeSuggestionEntity themeSuggestion = byId.get();

        themeSuggestionRepository.delete(themeSuggestion);

        return true;
    }
}
