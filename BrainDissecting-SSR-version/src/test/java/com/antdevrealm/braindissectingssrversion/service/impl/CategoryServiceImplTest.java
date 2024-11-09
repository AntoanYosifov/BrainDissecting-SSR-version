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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    private final String CATEGORY_NAME = "testName";

    private final long CATEGORY_ID = 1L;

    @Mock
    private CategoryRepository mockCategoryRepository;

    @Captor
    private ArgumentCaptor<CategoryEntity> categoryCaptor;

    private CategoryEntity categoryEntity;

    private CategoryServiceImpl toTest;

    @BeforeEach
    void setUp() {
        toTest = new CategoryServiceImpl(mockCategoryRepository);
        categoryEntity = new CategoryEntity();
        categoryEntity.setId(CATEGORY_ID);
        categoryEntity.setName(CATEGORY_NAME);
    }

    @Test
    void addCategory_ShouldSaveCategoryWithGivenName() {
        when(mockCategoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.empty());

        boolean result = toTest.addCategory(CATEGORY_NAME);

        verify(mockCategoryRepository).save(categoryCaptor.capture());
        CategoryEntity savedCategory = categoryCaptor.getValue();
        Assertions.assertTrue(result);
        Assertions.assertEquals(CATEGORY_NAME, savedCategory.getName());
    }

    @Test
    void addCategory_ShouldReturnFalseWhenCategoryAlreadyExists() {
        when(mockCategoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.of(categoryEntity));

        boolean result = toTest.addCategory(CATEGORY_NAME);

        Assertions.assertFalse(result);
    }

    @Test
    void removeCategory_ShouldReturnTrue_WhenCategoryExists() {
        when(mockCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(categoryEntity));

        boolean result = toTest.removeCategory(categoryEntity);

        Assertions.assertTrue(result);

    }

    @Test
    void removeCategory_ShouldReturnFalse_WhenCategoryDoesNotExist() {
        when(mockCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        boolean result = toTest.removeCategory(categoryEntity);

        Assertions.assertFalse(result);
    }

    @Test
    void getByName_ShouldReturnOptionalOfCategoryEntity_WhenEntityExists() {
        when(mockCategoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.of(categoryEntity));

        Optional<CategoryEntity> optionalCategory = toTest.getByName(CATEGORY_NAME);

        Assertions.assertTrue(optionalCategory.isPresent());
        Assertions.assertEquals(CATEGORY_NAME, optionalCategory.get().getName() );
    }

    @Test
    void getByName_ShouldReturnEmptyOptionalWhenEntityDoesNotExist() {
        when(mockCategoryRepository.findByName(CATEGORY_NAME)).thenReturn(Optional.empty());

        Optional<CategoryEntity> optionalCategory = toTest.getByName(CATEGORY_NAME);

        Assertions.assertTrue(optionalCategory.isEmpty());
    }

}
