package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    public GlobalControllerAdvice(CategoryService categoryService, ArticleService articleService) {
        this.categoryService = categoryService;
        this.articleService = articleService;
    }

    @ModelAttribute("categories")
    public List<CategoryEntity> addCategoriesModel() {
        return categoryService.getAll();
    }

    @ModelAttribute("pendingCount")
    public int pendingCount() {
        return articleService.countPendingArticles();
    }
}
