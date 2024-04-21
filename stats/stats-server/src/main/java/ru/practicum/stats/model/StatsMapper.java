package ru.practicum.stats.model;

import ru.practicum.stats.StatsDto;

public class StatsMapper {
    public static Stats dtoToStats(StatsDto statsDto) {
        return Stats.builder()
                .app(statsDto.getApp())
                .uri(statsDto.getUri())
                .ip(statsDto.getIp())
                .times(statsDto.getTimes())
                .build();
    }

    public static StatsDto statsToDto(Stats stats) {
        return StatsDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .ip(stats.getIp())
                .times(stats.getTimes())
                .build();
    }
}
