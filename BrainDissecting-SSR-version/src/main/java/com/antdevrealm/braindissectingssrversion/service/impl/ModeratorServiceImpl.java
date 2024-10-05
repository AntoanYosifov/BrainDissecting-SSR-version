package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.enums.ArticleStatus;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.service.ModeratorService;
import org.springframework.stereotype.Service;

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

        if (!articleEntity.getStatus().equals(ArticleStatus.PENDING)) {
            return false;
        }

        articleEntity.setStatus(ArticleStatus.APPROVED);
        articleRepository.save(articleEntity);
        return true;
    }

    @Override
    public boolean rejectArticle(Long articleId) {

        Optional<ArticleEntity> byId = articleRepository.findById(articleId);

        if (byId.isEmpty()) {
            return false;
        }

        ArticleEntity articleEntity = byId.get();

        if (!articleEntity.getStatus().equals(ArticleStatus.PENDING)) {
            return false;
        }

        articleRepository.delete(articleEntity);

        return true;
    }
}
