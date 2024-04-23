package ru.practicum.stats.service;

import ru.practicum.stats.StatsCountDto;
import ru.practicum.stats.StatsDto;

import java.util.List;

public interface StatsService {
    StatsDto createHit(StatsDto statsDto);

    List<StatsCountDto> getStats(String start, String end, List<String> uris, String unique);
}
