package ru.yandex.practicum.ewmmainservice.main.service.event;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Range start must be before range end");
        }

        if (rangeStart == null && rangeEnd == null) {
            Page<Event> events = eventRepository.findPublicEventsWithoutDate(
                    text, categories, paid, onlyAvailable, pageable);
            return events.stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        } else {
            Page<Event> events = eventRepository.findPublicEvents(
                    text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
            return events.stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long eventId) {
        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId)
                .orElseThrow(() -> new ActionConflictException("Event with id=" + eventId + " was not found!!!"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found.");
        }
        event.setViews(event.getViews() + 1);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }
}