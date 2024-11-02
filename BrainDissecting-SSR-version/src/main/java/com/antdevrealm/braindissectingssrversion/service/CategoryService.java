package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryEntity addCategory(String name);

    void removeCategory(Long id);

    void removeCategory(CategoryEntity category);

    Optional<CategoryEntity> getById(Long id);

    Optional<CategoryEntity> getByName(String name);

    List<CategoryEntity> getAll();

}
