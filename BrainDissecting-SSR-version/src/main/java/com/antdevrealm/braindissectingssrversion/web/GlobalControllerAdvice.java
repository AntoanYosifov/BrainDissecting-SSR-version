package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.service.CategoryService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CategoryService categoryService;

    public GlobalControllerAdvice(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ModelAttribute
    public void addCategoriesModel(Model model) {
        List<CategoryEntity> categories = categoryService.getAll();

        model.addAttribute("categories", categories);
    }
}
