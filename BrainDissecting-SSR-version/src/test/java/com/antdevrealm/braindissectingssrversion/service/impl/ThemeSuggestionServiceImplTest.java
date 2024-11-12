package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.ThemeSuggestionEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.UserEntity;
import com.antdevrealm.braindissectingssrversion.repository.CategoryRepository;
import com.antdevrealm.braindissectingssrversion.repository.ThemeSuggestionRepository;
import com.antdevrealm.braindissectingssrversion.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThemeSuggestionServiceImplTest {

    private final String THEME_NAME = "testName";

    private final long MODERATOR_ID = 1L;

    @Mock
    private ThemeSuggestionRepository mockThemeSuggestionRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private CategoryRepository mockCategoryRepository;

    @Captor
    private ArgumentCaptor<ThemeSuggestionEntity> themeCaptor;

    private ThemeSuggestionServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new ThemeSuggestionServiceImpl(mockThemeSuggestionRepository,
                mockUserRepository, mockCategoryRepository);
    }

    @Test
    void suggestTheme_ShouldReturnTrueWhenModeratorExistAndThemeIsNotAlreadySuggested() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(MODERATOR_ID);

        when(mockUserRepository.findById(MODERATOR_ID)).thenReturn(Optional.of(userEntity));
        when(mockThemeSuggestionRepository.existsByName(THEME_NAME.toLowerCase())).thenReturn(false);
        when(mockCategoryRepository.existsByName(THEME_NAME.toLowerCase())).thenReturn(false);

        boolean result = toTest.suggestTheme(THEME_NAME, MODERATOR_ID);

        verify(mockThemeSuggestionRepository).save(themeCaptor.capture());

        ThemeSuggestionEntity suggestedTheme = themeCaptor.getValue();
        Assertions.assertTrue(result);
        Assertions.assertEquals(THEME_NAME.toLowerCase(), suggestedTheme.getName());
        Assertions.assertEquals(MODERATOR_ID, suggestedTheme.getSuggestedBy().getId());
    }

}
