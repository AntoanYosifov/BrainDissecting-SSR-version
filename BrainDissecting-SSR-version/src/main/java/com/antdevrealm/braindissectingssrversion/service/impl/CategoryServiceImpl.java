package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.ArticleEntity;
import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.repository.ArticleRepository;
import com.antdevrealm.braindissectingssrversion.repository.CategoryRepository;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ArticleRepository articleRepository) {
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public CategoryEntity addCategory(String name) {
        CategoryEntity categoryEntity = new CategoryEntity(name);

        categoryEntity.setName(name);
       return categoryRepository.save(categoryEntity);

    }

    @Override
    public void removeCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryRepository.delete(category);
    }

    @Override
    public void assignCategory(Long articleId, Long categoryId) {
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow(() -> new RuntimeException("Article not found!"));
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found!"));


        articleEntity.getCategories().add(categoryEntity);
        articleRepository.save(articleEntity);

    }

    @Override
    public void unAssignCategory(Long articleId, Long categoryId) {
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow(() -> new RuntimeException("Article not found!"));
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found!"));

        articleEntity.getCategories().remove(categoryEntity);
        articleRepository.save(articleEntity);
    }

    @Override
    public Optional<CategoryEntity> getById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<CategoryEntity> getByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<CategoryEntity> getAll() {
        return categoryRepository.findAll();
    }
}
