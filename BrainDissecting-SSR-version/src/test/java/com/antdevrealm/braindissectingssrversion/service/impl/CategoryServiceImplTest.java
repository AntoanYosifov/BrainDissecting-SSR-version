package com.antdevrealm.braindissectingssrversion.service.impl;

import com.antdevrealm.braindissectingssrversion.model.entity.CategoryEntity;
import com.antdevrealm.braindissectingssrversion.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository mockCategoryRepository;

    @Captor
    private ArgumentCaptor<CategoryEntity> categoryCaptor;

    private CategoryServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new CategoryServiceImpl(mockCategoryRepository);
    }

    @Test
    void addCategory_ShouldSaveCategoryWithGivenName() {
        String categoryName = "testName";

        toTest.addCategory(categoryName);

        Mockito.verify(mockCategoryRepository).save(categoryCaptor.capture());
        CategoryEntity savedCategory = categoryCaptor.getValue();

        Assertions.assertEquals(categoryName, savedCategory.getName());
    }

}
