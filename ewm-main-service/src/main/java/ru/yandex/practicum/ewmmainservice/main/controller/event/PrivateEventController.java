package ru.yandex.practicum.ewmmainservice.main.controller.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventFullDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventShortDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.NewEventDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.ewmmainservice.main.service.event.PrivateEventServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {
    private final PrivateEventServiceImpl eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        log.info("Getting events for user id: {}, from: {}, size: {}", userId, from, size);
        return eventService.getUserEvents(userId, from, size, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Creating event for user id: {}, data: {}", userId, newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId, HttpServletRequest request) {
        log.info("Getting event id: {} for user id: {}", eventId, userId);
        return eventService.getUserEventById(userId, eventId, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        log.info("Updating event id: {} for user id: {}, data: {}", eventId, userId, updateRequest);
        return eventService.updateEventByUser(userId, eventId, updateRequest);
    }
}