package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    boolean addCategory(String name);

    boolean removeCategory(CategoryEntity category);

    Optional<CategoryEntity> getByName(String name);

    List<CategoryEntity> getAll();

}
