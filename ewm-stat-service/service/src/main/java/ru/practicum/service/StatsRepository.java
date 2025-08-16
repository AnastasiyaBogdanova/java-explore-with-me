package ru.practicum.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("select h.app as app, h.uri as uri, count(h.ip) as hits " +
            "from Hit as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<Stats> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("select h.app as app, h.uri as uri, count(distinct h.ip) as hits " +
            "from Hit as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<Stats> getUniqueStats(LocalDateTime start, LocalDateTime end);

    @Query("select h.app as app, h.uri as uri, count(h.ip) as hits " +
            "from Hit as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "and h.uri in :uris " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<Stats> getAllStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select h.app as app, h.uri as uri, count(distinct h.ip) as hits " +
            "from Hit as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "and h.uri in :uris " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<Stats> getUniqueStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}