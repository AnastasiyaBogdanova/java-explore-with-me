package ru.yandex.practicum.ewmmainservice.main.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.ewmmainservice.main.RequestStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "Request IDs cannot be null")
    private List<Long> requestIds;
    @NotNull(message = "Status cannot be null")
    private RequestStatus status;
}