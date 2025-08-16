package ru.practicum.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;
    private final HitMapper hitMapper;
    private final StatsMapper statsMapper;

    @Transactional
    public void postHit(HitDto hitDto) {
        statsRepository.save(hitMapper.toEntity(hitDto));
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<Stats> result;
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (uris == null || uris.isEmpty()) {
            result = unique ? statsRepository.getUniqueStats(start, end)
                    : statsRepository.getAllStats(start, end);
        } else {
            result = unique ? statsRepository.getUniqueStatsWithUris(start, end, uris)
                    : statsRepository.getAllStatsWithUris(start, end, uris);
        }
        return new ArrayList<>(statsMapper.toDto(result));
    }
}