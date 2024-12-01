package com.antdevrealm.braindissectingssrversion.repository;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional<ArticleEntity> findByTitle(String title);

    @Modifying
    @Query(value = "DELETE FROM `brain-dissecting-ssr`. user_favourite where favourite_id = :articleId", nativeQuery = true)
    void removeAllFromUsersFavourites(@Param("articleId") Long articleId);

    @Query("SELECT a FROM ArticleEntity a WHERE a.status = 'APPROVED'")
    List<ArticleEntity> findApprovedArticles();

    @Query("SELECT a FROM ArticleEntity a WHERE a.status = 'PENDING'")
    List<ArticleEntity> findPendingArticles();

    @Modifying
    void deleteAllByCategoriesContaining(CategoryEntity categoryEntity);

    int countByStatus(Status status);

    List<ArticleEntity> findAllByCategoriesContainingAndFavouriteIsTrue(CategoryEntity category);

    boolean existsByTitle(String title);
}
