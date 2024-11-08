package com.antdevrealm.braindissectingssrversion.service;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    void addCategory(String name);

    boolean removeCategory(CategoryEntity category);

    Optional<CategoryEntity> getByName(String name);

    List<CategoryEntity> getAll();

}
