package ru.yandex.practicum.ewmmainservice.main.service.compilation;

import ru.yandex.practicum.ewmmainservice.main.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest);
}