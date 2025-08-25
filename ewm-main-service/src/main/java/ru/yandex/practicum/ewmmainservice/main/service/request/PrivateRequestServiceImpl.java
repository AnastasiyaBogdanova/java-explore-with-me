package ru.yandex.practicum.ewmmainservice.main.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.EventState;
import ru.yandex.practicum.ewmmainservice.main.RequestStatus;
import ru.yandex.practicum.ewmmainservice.main.dto.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewmmainservice.main.dto.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewmmainservice.main.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.ewmmainservice.main.exception.ActionConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.ConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.RequestMapper;
import ru.yandex.practicum.ewmmainservice.main.mapper.UserMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Event;
import ru.yandex.practicum.ewmmainservice.main.model.Request;
import ru.yandex.practicum.ewmmainservice.main.model.User;
import ru.yandex.practicum.ewmmainservice.main.repository.EventRepository;
import ru.yandex.practicum.ewmmainservice.main.repository.RequestRepository;
import ru.yandex.practicum.ewmmainservice.main.service.user.AdminUserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final RequestRepository requestRepository;
    private final AdminUserService adminUserService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        adminUserService.userExists(userId);

        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        User requester = UserMapper.toUser(adminUserService.getUserById(userId));

        validateRequestCreation(userId, eventId, event);
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .build();


        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            Request savedRequest = requestRepository.save(request);

            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);

            return RequestMapper.toDto(savedRequest);
        } else {

            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

            if (!event.getRequestModeration()) {
                if (confirmedCount < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    Request savedRequest = requestRepository.save(request);

                    event.setConfirmedRequests((int) (confirmedCount + 1));
                    eventRepository.save(event);

                    return RequestMapper.toDto(savedRequest);
                } else {

                    throw new ActionConflictException("Participant limit reached for event " + eventId);
                }
            } else {
                request.setStatus(RequestStatus.PENDING);
                Request savedRequest = requestRepository.save(request);
                return RequestMapper.toDto(savedRequest);
            }
        }
    }

    private void validateRequestCreation(Long userId, Long eventId, Event event) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ActionConflictException("Request already exists for user " + userId + " and event " + eventId);
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ActionConflictException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ActionConflictException("Cannot participate in unpublished event");
        }


        if (event.getParticipantLimit() > 0 && !event.getRequestModeration()) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ActionConflictException("Participant limit reached for event " + eventId);
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        adminUserService.userExists(userId);
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        adminUserService.userExists(userId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("User can only cancel their own requests");
        }

        request.setStatus(RequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        return RequestMapper.toDto(updatedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        adminUserService.userExists(userId);

        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));


        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only event initiator can view participants");
        }

        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest) {
        adminUserService.userExists(userId);

        Event event = eventRepository.findByIdWithInitiatorAndCategory(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only event initiator can change request status");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ActionConflictException("Cannot confirm requests for unpublished event");
        }

        List<Request> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        if (requests.size() != updateRequest.getRequestIds().size()) {
            throw new NotFoundException("Some requests were not found");
        }

        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Request " + request.getId() + " does not belong to event " + eventId);
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ActionConflictException("Only pending requests can be changed");
            }
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {

            long currentConfirmedCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
            int participantLimit = event.getParticipantLimit();

            if (participantLimit > 0 && currentConfirmedCount >= participantLimit && !requests.isEmpty()) {
                throw new ActionConflictException("The participant limit has been reached");
            }

            processConfirmation(event, requests, confirmed, rejected);
        } else if (updateRequest.getStatus() == RequestStatus.REJECTED) {
            processRejection(requests, rejected);
        } else {
            throw new IllegalArgumentException("Invalid status: " + updateRequest.getStatus());
        }

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    private void processConfirmation(Event event, List<Request> requests,
                                     List<ParticipationRequestDto> confirmed,
                                     List<ParticipationRequestDto> rejected) {
        long confirmedCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        int participantLimit = event.getParticipantLimit();

        for (Request request : requests) {
            if (participantLimit == 0 || confirmedCount < participantLimit) {
                request.setStatus(RequestStatus.CONFIRMED);
                requestRepository.save(request);
                confirmed.add(RequestMapper.toDto(request));
                confirmedCount++;

                event.setConfirmedRequests((int) confirmedCount);
                eventRepository.save(event);
            } else {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejected.add(RequestMapper.toDto(request));
            }
        }
    }

    private void processRejection(List<Request> requests, List<ParticipationRequestDto> rejected) {
        for (Request request : requests) {
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            rejected.add(RequestMapper.toDto(request));
        }
    }
}