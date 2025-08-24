package ru.yandex.practicum.ewmmainservice.main.service.request;

import ru.yandex.practicum.ewmmainservice.main.dto.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewmmainservice.main.dto.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewmmainservice.main.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);
}