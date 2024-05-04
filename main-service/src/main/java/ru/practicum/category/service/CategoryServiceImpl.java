package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.validator.CategoryValidator;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;

    @Autowired
    private final EventRepository eventRepository;

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable).stream().map(CategoryMapper::categoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category response = CategoryValidator.validateCategoryExists(categoryRepository, catId);
        return CategoryMapper.categoryToDto(response);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category current = categoryRepository.findByName(categoryDto.getName());
        if (current != null) {
            throw new ServerErrorException("Category with name " + categoryDto.getName() + " already exists");
        }
        return CategoryMapper.categoryToDto(categoryRepository.save(CategoryMapper.categoryFromDto(categoryDto)));
    }

    @Override
    public void deleteCategory(long catId) {
        List<Event> events = eventRepository.findByCategoryId(catId);
        if (events.size() > 0) {
            throw new ServerErrorException("The category with id=" + catId + " is not empty");
        }
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            throw new ObjectNotFoundException("Category with id=" + catId + " was not found");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        CategoryValidator.validateCategoryExists(categoryRepository, catId);
        Category current = categoryRepository.findByName(categoryDto.getName());
        if (current != null) {
            if (!Objects.equals(current.getId(), catId)) {
                throw new ServerErrorException("Category with name \" + categoryDto.getName() + \" already exists");
            }
        }
        return CategoryMapper.categoryToDto(categoryRepository.save(CategoryMapper.categoryFromDto(categoryDto)));
    }
}
