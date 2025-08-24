package ru.yandex.practicum.ewmmainservice.main.mapper;


import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.dto.category.NewCategoryDto;
import ru.yandex.practicum.ewmmainservice.main.model.Category;

@UtilityClass
public class CategoryMapper {
    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}