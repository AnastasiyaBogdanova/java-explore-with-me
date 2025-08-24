package ru.yandex.practicum.ewmmainservice.main.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Title length must be between 1 and 50 characters")
    private String title;
}