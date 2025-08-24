package ru.yandex.practicum.ewmmainservice.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewmmainservice.main.EventState;
import ru.yandex.practicum.ewmmainservice.main.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {


    boolean existsById(Long eventId);


    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category " +
            "WHERE (COALESCE(:users, NULL) IS NULL OR e.initiator.id IN :users) " +
            "AND (COALESCE(:states, NULL) IS NULL OR e.state IN :states) " +
            "AND (COALESCE(:categories, NULL) IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= :rangeEnd)")
    Page<Event> findAdminEvents(@Param("users") List<Long> users,
                                @Param("states") List<EventState> states,
                                @Param("categories") List<Long> categories,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category " +
            "WHERE (COALESCE(:users, NULL) IS NULL OR e.initiator.id IN :users) " +
            "AND (COALESCE(:states, NULL) IS NULL OR e.state IN :states) " +
            "AND (COALESCE(:categories, NULL) IS NULL OR e.category.id IN :categories) ")
    Page<Event> findAdminEventsWithoutDate(@Param("users") List<Long> users,
                                           @Param("states") List<EventState> states,
                                           @Param("categories") List<Long> categories,
                                           Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (COALESCE(:text, '') = '' OR " +
            "     LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "     LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (COALESCE(:categories, NULL) IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable IS NULL OR " +
            "     (e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit))")
    Page<Event> findPublicEvents(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 @Param("onlyAvailable") Boolean onlyAvailable,
                                 Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (COALESCE(:text, '') = '' OR " +
            "     LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "     LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (COALESCE(:categories, NULL) IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:onlyAvailable IS NULL OR " +
            "     (e.participantLimit = 0 OR e.confirmedRequests < e.participantLimit))")
    Page<Event> findPublicEventsWithoutDate(@Param("text") String text,
                                            @Param("categories") List<Long> categories,
                                            @Param("paid") Boolean paid,
                                            @Param("onlyAvailable") Boolean onlyAvailable,
                                            Pageable pageable);


    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category WHERE e.initiator.id = :userId")
    Page<Event> findByInitiatorId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category WHERE e.id = :eventId AND e.initiator.id = :userId")
    Optional<Event> findByIdAndInitiatorId(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator LEFT JOIN FETCH e.category WHERE e.id = :eventId")
    Optional<Event> findByIdWithInitiatorAndCategory(@Param("eventId") Long eventId);


    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.initiator WHERE e.id = :eventId AND e.initiator.id = :userId")
    Optional<Event> findByIdAndInitiatorIdWithInitiator(@Param("eventId") Long eventId,
                                                        @Param("userId") Long userId);

}