package ru.yandex.practicum.ewmmainservice.main.controller.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.dto.category.NewCategoryDto;
import ru.yandex.practicum.ewmmainservice.main.service.category.AdminCategoryServiceImpl;


@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoryController {
    private final AdminCategoryServiceImpl categoryServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Creating new category: {}", newCategoryDto);
        return categoryServiceImpl.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive Long catId) {
        log.info("Deleting category with id: {}", catId);
        categoryServiceImpl.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable @Positive Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Updating category with id: {}, data: {}", catId, categoryDto);
        return categoryServiceImpl.updateCategory(catId, categoryDto);
    }
}