package com.antdevrealm.braindissectingssrversion.repository;

import com.antdevrealm.braindissectingssrversion.model.entity.ThemeSuggestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeSuggestionRepository extends JpaRepository<ThemeSuggestionEntity, Long> {
    List<ThemeSuggestionEntity> findAllBySuggestedById(Long userId);
}
