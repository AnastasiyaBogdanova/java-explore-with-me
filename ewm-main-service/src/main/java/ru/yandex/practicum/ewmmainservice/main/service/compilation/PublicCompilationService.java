package ru.yandex.practicum.ewmmainservice.main.service.compilation;

import ru.yandex.practicum.ewmmainservice.main.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}