package ru.yandex.practicum.ewmmainservice.main.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 1, max = 50, message = "Name length must be between 1 and 50 characters")
    private String name;
}