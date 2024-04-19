package ru.practicum.stats.model;

import org.springframework.stereotype.Component;
import ru.practicum.stats.StatsDto;

@Component
public class StatsMapper {
    public Stats dtoToStats(StatsDto statsDto) {
        return Stats.builder()
                .app(statsDto.getApp())
                .uri(statsDto.getUri())
                .ip(statsDto.getIp())
                .times(statsDto.getTimes())
                .build();
    }

    public StatsDto statsToDto(Stats stats) {
        return StatsDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .ip(stats.getIp())
                .times(stats.getTimes())
                .build();
    }
}
