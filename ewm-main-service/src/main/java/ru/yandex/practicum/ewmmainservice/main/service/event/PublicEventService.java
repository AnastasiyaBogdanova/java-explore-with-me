package ru.yandex.practicum.ewmmainservice.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventFullDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long eventId,HttpServletRequest request);
}