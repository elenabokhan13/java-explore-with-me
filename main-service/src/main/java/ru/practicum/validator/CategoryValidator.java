package ru.practicum.validator;

import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.exception.ObjectNotFoundException;

public class CategoryValidator {
    public static Category validateCategoryExists(CategoryRepository categoryRepository, Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + id + " was not found"));
    }
}
