package ru.yandex.practicum.ewmmainservice.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventFullDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventShortDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.NewEventDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventService {

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto getUserEventById(Long userId, Long eventId, HttpServletRequest request);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size, HttpServletRequest request);
}