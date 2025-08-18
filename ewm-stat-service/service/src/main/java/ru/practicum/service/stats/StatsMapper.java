package ru.practicum.service.stats;

import org.mapstruct.Mapper;
import ru.practicum.dto.StatsDto;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public class StatsMapper {
    public StatsDto toDto(Stats stats) {
        return StatsDto.builder()
                .uri(stats.getUri())
                .app(stats.getApp())
                .hits(stats.getHits())
                .build();
    }

    public Collection<StatsDto> toDto(Collection<Stats> stats) {
        return stats.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}