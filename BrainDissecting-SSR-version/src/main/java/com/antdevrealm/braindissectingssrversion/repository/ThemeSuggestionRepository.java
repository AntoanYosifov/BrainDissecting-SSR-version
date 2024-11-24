package com.antdevrealm.braindissectingssrversion.repository;

import com.antdevrealm.braindissectingssrversion.model.entity.ThemeSuggestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeSuggestionRepository extends JpaRepository<ThemeSuggestionEntity, Long> {
    Optional<ThemeSuggestionEntity> findByName(String name);
    List<ThemeSuggestionEntity> findAllBySuggestedById(Long userId);
    boolean existsByName(String name);
}
