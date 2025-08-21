package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(HitDto hitDto) {
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(FORMATTER),
                "end", end.format(FORMATTER),
                "unique", unique
        );

        if (uris != null && !uris.isEmpty()) {
            parameters = Map.of(
                    "start", start.format(FORMATTER),
                    "end", end.format(FORMATTER),
                    "uris", String.join(",", uris),
                    "unique", unique
            );
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", null, parameters);
        }

        return get("/stats?start={start}&end={end}&unique={unique}", null, parameters);
    }
}