package ru.yandex.practicum.ewmmainservice.main.service.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.yandex.practicum.ewmmainservice.main.model.Event;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticServiceImp implements StatisticService {
    private final StatsClient client;
    private final String applicationName;

    @Override
    @Async
    public void postHit(String uri, String ip) {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("Statistic service, save hit: uri={}, ip={}, app={}, timestamp={}", uri, ip, applicationName, timestamp);
        HitDto hitDto = new HitDto(applicationName, uri, ip, timestamp);
        client.addHit(hitDto);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Statistic service, getting events statistic: start={}, end={}, uris={}, unique={}", start, end, uris, unique);


        ResponseEntity<Object> response = client.getStats(start, end, uris, unique);

        if (response.getBody() instanceof List) {
            List<?> bodyList = (List<?>) response.getBody();
            List<StatsDto> statsDtos = bodyList.stream()
                    .filter(item -> item instanceof StatsDto)
                    .map(item -> (StatsDto) item)
                    .collect(Collectors.toList());

            log.info("Statistic service, getting events statistic: result={}", statsDtos);
            return statsDtos;
        }
        return Collections.emptyList();
    }

    @Override
    public Map<Long, Long> getStatsByEvents(List<Event> events, boolean unique) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%d", event.getId()))
                .collect(Collectors.toList());

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now().minusYears(1));

        LocalDateTime end = LocalDateTime.now().plusMinutes(1);

        List<StatsDto> statsDtos = getStats(start, end, uris, unique);
        return transformStatistic(statsDtos);
    }

    private Map<Long, Long> transformStatistic(List<StatsDto> statsDtos) {
        Map<Long, Long> result = new HashMap<>();
        if (statsDtos != null) {
            for (StatsDto dto : statsDtos) {
                try {
                    String uri = dto.getUri();
                    if (uri.startsWith("/events/")) {
                        Long id = Long.parseLong(uri.substring("/events/".length()));
                        result.put(id, dto.getHits());
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid event ID in URI: {}", dto.getUri());
                }
            }
        }
        log.info("Statistic service, transformed result={}", result);
        return result;
    }
}