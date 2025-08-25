package ru.yandex.practicum.ewmmainservice.main.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.dto.category.NewCategoryDto;
import ru.yandex.practicum.ewmmainservice.main.exception.ActionConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.CategoryMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Category;
import ru.yandex.practicum.ewmmainservice.main.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ActionConflictException("Category with name '" + newCategoryDto.getName() + "' already exists.");
        }

        Category category = CategoryMapper.toEntity(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id=" + catId + " was not found");
        }

        Long eventsCount = categoryRepository.countEventsByCategoryId(catId);
        if (eventsCount > 0) {
            throw new ActionConflictException("The category is not empty");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        if (!categoryDto.getName().equals(category.getName()) &&
                categoryRepository.existsByNameAndIdNot(categoryDto.getName(), catId)) {
            throw new ActionConflictException("Category with name '" + categoryDto.getName() + "' already exists");
        }

        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.toDto(updatedCategory);
    }
}