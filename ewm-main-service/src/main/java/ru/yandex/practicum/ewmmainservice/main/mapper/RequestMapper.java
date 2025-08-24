package ru.yandex.practicum.ewmmainservice.main.mapper;

import ru.yandex.practicum.ewmmainservice.main.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.ewmmainservice.main.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
