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

import java.util.Optional;

import static org.mockito.Mockito.when;

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

        when(mockCategoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        boolean result = toTest.addCategory(categoryName);

        Mockito.verify(mockCategoryRepository).save(categoryCaptor.capture());
        CategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertTrue(result);
        Assertions.assertEquals(categoryName, savedCategory.getName());
    }

    @Test
    void addCategory_ShouldReturnFalseWhenCategoryAlreadyExists() {
        String categoryName = "testName";

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryName);

        when(mockCategoryRepository.findByName(categoryName)).thenReturn(Optional.of(categoryEntity));

        boolean result = toTest.addCategory(categoryName);

        Assertions.assertFalse(result);
    }

}
