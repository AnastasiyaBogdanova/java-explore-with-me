package ru.yandex.practicum.ewmmainservice.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewmmainservice.main.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.category.id = :categoryId")
    Long countEventsByCategoryId(@Param("categoryId") Long categoryId);

    Page<Category> findAll(Pageable pageable);
}