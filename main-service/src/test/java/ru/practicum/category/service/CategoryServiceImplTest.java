package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/schema.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceImplTest {
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, eventRepository);
    }


    @Test
    void getCategory() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("name1")
                .build();
        categoryService.createCategory(categoryDto);
        CategoryDto categoryDtoSaved = categoryService.getCategory(1L);

        assertThat(categoryDtoSaved.getId().equals(1L));
        assertThat(categoryDtoSaved.getName().equals("name1"));
    }

    @Test
    void createCategory() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("name1")
                .build();
        CategoryDto categoryDtoSaved = categoryService.createCategory(categoryDto);

        assertThat(categoryDtoSaved.getId().equals(1L));
        assertThat(categoryDtoSaved.getName().equals("name1"));
    }

    @Test
    void createCategoryThrowsException() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("name1")
                .build();

        categoryService.createCategory(categoryDto);

        assertThrows(ServerErrorException.class, () -> categoryService.createCategory(categoryDto));
    }

    @Test
    void deleteCategory() {
        assertThrows(ObjectNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void updateCategory() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("name1")
                .build();

        CategoryDto categoryDtoTwo = CategoryDto.builder()
                .name("name2")
                .build();

        categoryService.createCategory(categoryDto);

        CategoryDto categoryUpdated = categoryService.updateCategory(1L, categoryDtoTwo);

        assertThat(categoryUpdated.getId().equals(1L));
        assertThat(categoryUpdated.getName().equals("name2"));
    }
}