package ru.practicum.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
    @NotNull(message = "app should not be empty")
    private String app;
    @NotNull(message = "uri should not be empty")
    private String uri;
    @NotNull(message = "ip should not be empty")
    private String ip;
    @NotNull(message = "timestamp should not be empty")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}