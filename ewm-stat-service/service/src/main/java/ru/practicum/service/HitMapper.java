package ru.practicum.service;

import org.mapstruct.Mapper;
import ru.practicum.dto.HitDto;

@Mapper(componentModel = "spring")
public class HitMapper {

    public Hit toEntity(HitDto hitDto) {
        return Hit.builder()
                .uri(hitDto.getUri())
                .app(hitDto.getApp())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }
}
