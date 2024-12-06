package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.Status;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.service.ModeratorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeratorServiceImpl implements ModeratorService {

    private final ArticleRepository articleRepository;

    public ModeratorServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public boolean approveArticle(Long articleId) {
        Optional<ArticleEntity> byId = articleRepository.findById(articleId);

        if (byId.isEmpty()) {
            return false;
        }

        ArticleEntity articleEntity = byId.get();

        if (!articleEntity.getStatus().equals(Status.PENDING)) {
            return false;
        }

        articleEntity.setStatus(Status.APPROVED);
        articleRepository.save(articleEntity);
        return true;
    }

    @Override
    public boolean approveAllArticles() {
        if(articleRepository.count() < 1L) {
            return false;
        }

        List<ArticleEntity> pendingArticles = articleRepository.findPendingArticles();

        if(pendingArticles.isEmpty()) {
            return false;
        }

        pendingArticles.forEach(articleEntity -> {
            articleEntity.setStatus(Status.APPROVED);
            articleRepository.save(articleEntity);
        });

        return true;
    }

    @Override
    public boolean rejectArticle(Long articleId) {
        Optional<ArticleEntity> byId = articleRepository.findById(articleId);

        if (byId.isEmpty()) {
            return false;
        }

        ArticleEntity articleEntity = byId.get();

        if (!articleEntity.getStatus().equals(Status.PENDING)) {
            return false;
        }

        articleRepository.delete(articleEntity);

        return true;
    }

    @Override
    public boolean rejectAllArticles() {
        if(articleRepository.count() < 1L) {
            return false;
        }

        List<ArticleEntity> pendingArticles = articleRepository.findPendingArticles();

        if(pendingArticles.isEmpty()) {
            return false;
        }

        articleRepository.deleteAll(pendingArticles);

        return true;
    }
}
