package ru.practicum.category.dto;

import ru.practicum.category.model.Category;

public class CategoryMapper {
    public static Category categoryfromDto(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto categoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
