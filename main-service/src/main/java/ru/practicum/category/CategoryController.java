package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    @GetMapping(path = "/categories")
    public Collection<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпойнту /categories для получения списка категорий");
        return categoryService.getCategories(from, size);
    }

    @GetMapping(path = "/categories/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("Получен запрос к эндпойнту /categories для получения категории по id {}", catId);
        return categoryService.getCategory(catId);
    }

    @PostMapping(path = "/admin/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Получен запрос к эндпойнту /admin/categories для создания категории");
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/admin/categories/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        log.info("Получен запрос к эндпойнту /admin/categories для удаления категории по id {}", catId);
        categoryService.deleteCategory(catId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/admin/categories/{catId}")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto, @PathVariable Long catId) {
        log.info("Получен запрос к эндпойнту /admin/categories для обновления категории по id {}", catId);
        return categoryService.updateCategory(catId, categoryDto);
    }
}
