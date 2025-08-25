package ru.yandex.practicum.ewmmainservice.main.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.EventState;
import ru.yandex.practicum.ewmmainservice.main.StateAction;
import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.EventFullDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.ewmmainservice.main.exception.ActionConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.ConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.CategoryMapper;
import ru.yandex.practicum.ewmmainservice.main.mapper.EventMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Event;
import ru.yandex.practicum.ewmmainservice.main.model.Location;
import ru.yandex.practicum.ewmmainservice.main.repository.EventRepository;
import ru.yandex.practicum.ewmmainservice.main.service.category.PublicCategoryService;
import ru.yandex.practicum.ewmmainservice.main.service.comment.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final PublicCategoryService categoryService;
    private final CommentService commentService;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                             Integer from, Integer size) {

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<Long> processedUsers = (users != null && !users.isEmpty()) ? users : null;
        List<Long> processedCategories = (categories != null && !categories.isEmpty()) ? categories : null;

        List<EventState> eventStates = null;
        if (states != null && !states.isEmpty()) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        if (rangeStart == null && rangeEnd == null) {
            Page<Event> events = eventRepository.findAdminEventsWithoutDate(
                    processedUsers, eventStates, processedCategories, pageable);
            return events.stream()
                    .map(EventMapper::toFullDto)
                    .collect(Collectors.toList());
        } else {
            Page<Event> events = eventRepository.findAdminEvents(
                    processedUsers, eventStates, processedCategories, rangeStart, rangeEnd, pageable);
            return events.stream()
                    .map(EventMapper::toFullDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found!!"));

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Event date must be at least 1 hour from now");
        }

        if (updateRequest.getStateAction() != null) {
            if (StateAction.PUBLISH_EVENT == updateRequest.getStateAction()) {
                if (event.getState() != EventState.PENDING) {
                    throw new ActionConflictException("Cannot publish the event because it's not in the right state: PENDING");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (StateAction.REJECT_EVENT == updateRequest.getStateAction()) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ActionConflictException("Cannot reject the event because it's already published");
                }
                event.setState(EventState.CANCELED);
            }
        }

        updateEventFields(event, updateRequest);
        Event updatedEvent = eventRepository.save(event);
        return EventMapper.toFullDto(updatedEvent);
    }

    private void updateEventFields(Event event, UpdateEventAdminRequest request) {
        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
        if (request.getTitle() != null) event.setTitle(request.getTitle());

        if (request.getCategory() != null) {
            CategoryDto category = categoryService.getCategoryById(request.getCategory());
            event.setCategory(CategoryMapper.toEntity(category));
        }

        if (request.getLocation() != null) {
            event.setLocation(new Location(
                    request.getLocation().getLat(),
                    request.getLocation().getLon()
            ));
        }
    }
}