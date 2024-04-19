package ru.practicum.stats;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class StatsDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime times;
}
