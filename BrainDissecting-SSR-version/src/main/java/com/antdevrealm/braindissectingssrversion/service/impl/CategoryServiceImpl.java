package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.repository.CategoryRepository;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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
