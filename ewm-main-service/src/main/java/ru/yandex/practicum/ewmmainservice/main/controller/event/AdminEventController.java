package ru.yandex.practicum.ewmmainservice.main.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventFullDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.ewmmainservice.main.service.event.AdminEventServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {
    private final AdminEventServiceImpl eventService;

    @GetMapping
    public List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) String users,
            @RequestParam(required = false) String states,
            @RequestParam(required = false) String categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        List<Long> usersList = parseLongList(users);
        List<String> statesList = parseStringList(states);
        List<Long> categoriesList = parseLongList(categories);

        log.info("Getting events for admin - users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                usersList, statesList, categoriesList, rangeStart, rangeEnd, from, size);

        return eventService.getAdminEvents(usersList, statesList, categoriesList, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        log.info("Admin updating event id: {}, data: {}", eventId, updateRequest);
        return eventService.updateEventByAdmin(eventId, updateRequest);
    }

    private List<String> parseStringList(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(param.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Long> parseLongList(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(param.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}