package ru.yandex.practicum.ewmmainservice.main.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.ewmmainservice.main.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}