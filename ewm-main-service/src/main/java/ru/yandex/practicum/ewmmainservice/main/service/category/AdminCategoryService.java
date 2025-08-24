package ru.yandex.practicum.ewmmainservice.main.service.category;

import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.dto.category.NewCategoryDto;

public interface AdminCategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);
}