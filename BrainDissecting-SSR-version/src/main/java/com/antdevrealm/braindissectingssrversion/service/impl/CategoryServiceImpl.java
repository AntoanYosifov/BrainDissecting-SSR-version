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
    public boolean addCategory(String name) {
        Optional<CategoryEntity> optionalCategory = categoryRepository.findByName(name);

        if(optionalCategory.isPresent()) {
            return false;
        }

        CategoryEntity categoryEntity = new CategoryEntity(name.toLowerCase());

        categoryEntity.setName(name);
        categoryRepository.save(categoryEntity);
        return true;
    }

    @Override
    public boolean removeCategory(CategoryEntity category) {
        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(category.getId());

        if(optionalCategory.isEmpty()) {
            return false;
        }

        categoryRepository.deleteById(category.getId());
        return true;
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
