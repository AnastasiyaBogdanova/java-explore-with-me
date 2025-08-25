package ru.yandex.practicum.ewmmainservice.main.mapper;


import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.dto.category.NewCategoryDto;
import ru.yandex.practicum.ewmmainservice.main.model.Category;

@UtilityClass
public class CategoryMapper {
    public static Category toEntity(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toEntity(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}