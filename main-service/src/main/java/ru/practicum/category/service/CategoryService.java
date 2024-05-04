package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {
    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long catId);

    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);
}
