package ru.yandex.practicum.ewmmainservice.main.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.CompilationDto;
import ru.yandex.practicum.ewmmainservice.main.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.ewmmainservice.main.model.Compilation;

import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static Compilation toEntity(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false)
                .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());

        if (compilation.getEvents() != null) {
            dto.setEvents(compilation.getEvents().stream()
                    .map(EventMapper::toShortDto)
                    .collect(Collectors.toSet()));
        } else {
            dto.setEvents(Collections.emptySet());
        }

        return dto;
    }
}