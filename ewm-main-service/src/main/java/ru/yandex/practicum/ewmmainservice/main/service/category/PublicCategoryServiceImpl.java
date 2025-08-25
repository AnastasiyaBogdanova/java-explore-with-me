package ru.yandex.practicum.ewmmainservice.main.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.CategoryMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Category;
import ru.yandex.practicum.ewmmainservice.main.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of((from > 0) ? from / size : 0, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.toDto(category);
    }
}