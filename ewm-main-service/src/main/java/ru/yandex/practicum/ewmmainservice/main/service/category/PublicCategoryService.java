package ru.yandex.practicum.ewmmainservice.main.service.category;

import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}