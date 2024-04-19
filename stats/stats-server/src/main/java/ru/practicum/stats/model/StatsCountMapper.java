package ru.practicum.stats.model;

import org.springframework.stereotype.Component;
import ru.practicum.stats.StatsCountDto;


@Component
public class StatsCountMapper {
    public StatsCountDto statsCountToDto(StatsCount statsCount) {
        return StatsCountDto.builder()
                .app(statsCount.getApp())
                .uri(statsCount.getUri())
                .hits(statsCount.getHits())
                .build();
    }
}
