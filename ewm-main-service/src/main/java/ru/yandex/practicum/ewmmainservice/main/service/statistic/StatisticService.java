package ru.yandex.practicum.ewmmainservice.main.service.statistic;

import ru.practicum.dto.StatsDto;
import ru.yandex.practicum.ewmmainservice.main.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticService {
    void postHit(String uri, String ip);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    Map<Long, Long> getStatsByEvents(List<Event> events, boolean unique);

}