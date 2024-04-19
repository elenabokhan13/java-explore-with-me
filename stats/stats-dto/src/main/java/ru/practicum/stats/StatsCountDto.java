package ru.practicum.stats;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StatsCountDto {
    private String app;
    private String uri;
    private Integer hits;
}
