package ru.practicum.stats.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.stats.StatsCountDto;
import ru.practicum.stats.StatsDto;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.model.StatsCount;
import ru.practicum.stats.model.StatsCountMapper;
import ru.practicum.stats.model.StatsMapper;
import ru.practicum.stats.storage.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    private StatsService statsService;

    @Mock
    private StatsRepository statsRepository;

    @Mock
    private StatsMapper statsMapper;
    @Mock
    private StatsCountMapper statsCountMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {
        statsService = new StatsServiceImpl(statsRepository, statsMapper, statsCountMapper);
    }


    @Test
    void createHitTest() {
        StatsDto statsDto = StatsDto.builder()
                .app("app-test")
                .uri("/test")
                .ip("0.0.0.0")
                .times(LocalDateTime.now())
                .build();
        Stats stats = Stats.builder()
                .app("app-test")
                .uri("/test")
                .ip("0.0.0.0")
                .times(LocalDateTime.now())
                .build();

        when(statsMapper.dtoToStats(any())).thenReturn(stats);
        when(statsRepository.save(any())).thenReturn(stats);
        when(statsMapper.statsToDto(any())).thenReturn(statsDto);

        StatsDto response = statsService.createHit(statsDto);
        assertEquals(response.getUri(), statsDto.getUri());
        assertEquals(response.getApp(), statsDto.getApp());
        assertEquals(response.getIp(), statsDto.getIp());
    }

    @Test
    void getStatsTest() {
        StatsCountDto countDto = StatsCountDto.builder()
                .uri("/test")
                .app("app-test")
                .hits(2)
                .build();
        StatsCount count = StatsCount.builder()
                .uri("/test")
                .app("app-test")
                .hits(2)
                .build();
        Stats stats = Stats.builder()
                .app("app-test")
                .uri("/test")
                .ip("0.0.0.0")
                .times(LocalDateTime.now())
                .build();

        when(statsRepository.getByParametersNoUris(any(), any())).thenReturn(List.of(stats));
        when(statsCountMapper.statsCountToDto(any())).thenReturn(countDto);

        List<Stats> responseCurrent = statsRepository
                .getByParametersNoUris(LocalDateTime.of(2000, 03, 01, 01, 01),
                        LocalDateTime.of(2025, 03, 01, 01, 01));

        StatsCountDto response = statsCountMapper.statsCountToDto(count);

        assertEquals(response.getUri(), count.getUri());
        assert (responseCurrent.contains(stats));
    }
}