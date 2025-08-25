package ru.yandex.practicum.ewmmainservice.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.EventState;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventFullDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventShortDto;
import ru.yandex.practicum.ewmmainservice.main.exception.ActionConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.EventMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Event;
import ru.yandex.practicum.ewmmainservice.main.repository.EventRepository;
import ru.yandex.practicum.ewmmainservice.main.service.comment.CommentService;
import ru.yandex.practicum.ewmmainservice.main.service.statistic.StatisticService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final CommentService commentService;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer from, Integer size,
                                               HttpServletRequest request) {
        statisticService.postHit(request.getRequestURI(), request.getRemoteAddr());

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Range start must be before range end");
        }

        Page<Event> events;
        if (rangeStart == null && rangeEnd == null) {
            events = eventRepository.findPublicEventsWithoutDate(
                    text, categories, paid, onlyAvailable, pageable);
        } else {
            events = eventRepository.findPublicEvents(
                    text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        }

        Map<Long, Long> viewsStats = statisticService.getStatsByEvents(events.getContent(), false);

        return events.stream()
                .map(event -> {
                    EventShortDto dto = EventMapper.toShortDto(event);
                    long statsViews = viewsStats.getOrDefault(event.getId(), 0L);
                    dto.setViews(statsViews + event.getViews());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId)
                .orElseThrow(() -> new ActionConflictException("Event with id=" + eventId + " was not found!!!"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found.");
        }

        statisticService.postHit("/events/" + eventId, request.getRemoteAddr());

        Map<Long, Long> viewsStats = statisticService.getStatsByEvents(List.of(event), true);
        long statsViews = viewsStats.getOrDefault(eventId, 0L);

        event.setViews(statsViews + 1);
        Event savedEvent = eventRepository.save(event);

        EventFullDto dto = EventMapper.toFullDto(savedEvent);

        return dto;
    }
}