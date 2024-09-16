package com.antdevrealm.braindissectingssrversion.repository;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional<ArticleEntity> findByTitle(String title);
}
