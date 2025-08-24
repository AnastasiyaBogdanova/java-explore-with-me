package ru.yandex.practicum.ewmmainservice.main.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewmmainservice.main.EventState;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000, columnDefinition = "TEXT")
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;

    @Column(name = "created_on", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @Column(length = 7000, columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Embedded
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit = 0;

    @Column(name = "published_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration = true;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(nullable = false, length = 120)
    private String title;

    private Long views = 0L;
}



