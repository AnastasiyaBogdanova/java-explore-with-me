package ru.yandex.practicum.ewmmainservice.main.service.event;

import ru.yandex.practicum.ewmmainservice.main.dto.event.EventFullDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);
}