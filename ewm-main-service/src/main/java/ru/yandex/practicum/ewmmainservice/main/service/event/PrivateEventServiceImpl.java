package ru.yandex.practicum.ewmmainservice.main.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.EventState;
import ru.yandex.practicum.ewmmainservice.main.StateAction;
import ru.yandex.practicum.ewmmainservice.main.dto.category.CategoryDto;
import ru.yandex.practicum.ewmmainservice.main.dto.event.*;
import ru.yandex.practicum.ewmmainservice.main.dto.user.UserDto;
import ru.yandex.practicum.ewmmainservice.main.exception.ActionConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.ConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.CategoryMapper;
import ru.yandex.practicum.ewmmainservice.main.mapper.EventMapper;
import ru.yandex.practicum.ewmmainservice.main.mapper.UserMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Event;
import ru.yandex.practicum.ewmmainservice.main.model.Location;
import ru.yandex.practicum.ewmmainservice.main.repository.EventRepository;
import ru.yandex.practicum.ewmmainservice.main.service.category.PublicCategoryService;
import ru.yandex.practicum.ewmmainservice.main.service.user.AdminUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final AdminUserService userService;
    private final PublicCategoryService categoryService;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        UserDto initiator = userService.getUserById(userId);
        if (initiator == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        CategoryDto category = categoryService.getCategoryById(newEventDto.getCategory());
        if (category == null) {
            throw new NotFoundException("Category not found with id: " + newEventDto.getCategory());
        }

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(UserMapper.toUser(initiator));
        event.setCategory(CategoryMapper.toCategory(category));
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0L);
        event.setCreatedOn(LocalDateTime.now());

        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found!"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ActionConflictException("Only pending or canceled events can be changed");
        }

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        updateEventFields(event, updateRequest);

        if (updateRequest.getStateAction() != null) {
            if (StateAction.SEND_TO_REVIEW == updateRequest.getStateAction()) {
                event.setState(EventState.PENDING);
            } else if (StateAction.CANCEL_REVIEW == updateRequest.getStateAction()) {
                event.setState(EventState.CANCELED);
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorIdWithInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private void updateEventFields(Event event, UpdateEventUserRequest request) {
        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getPaid() != null) event.setPaid(request.getPaid());
        if (request.getParticipantLimit() != null) event.setParticipantLimit(request.getParticipantLimit());
        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());
        if (request.getTitle() != null) event.setTitle(request.getTitle());

        if (request.getCategory() != null) {
            CategoryDto category = categoryService.getCategoryById(request.getCategory());
            event.setCategory(CategoryMapper.toCategory(category));
        }

        if (request.getLocation() != null) {
            event.setLocation(new Location(
                    request.getLocation().getLat(),
                    request.getLocation().getLon()
            ));
        }
    }
}