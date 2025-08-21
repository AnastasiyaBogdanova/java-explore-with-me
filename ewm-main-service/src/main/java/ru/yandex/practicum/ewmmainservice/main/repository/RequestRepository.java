package ru.yandex.practicum.ewmmainservice.main.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewmmainservice.main.RequestStatus;
import ru.yandex.practicum.ewmmainservice.main.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {


    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByEventId(Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
}