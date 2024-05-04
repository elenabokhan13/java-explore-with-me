package ru.practicum.stats.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.stats.StatsCountDto;
import ru.practicum.stats.StatsDto;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    private StatsService statsService;

    @Mock
    private StatsRepository statsRepository;

    @BeforeEach
    public void setUp() {
        statsService = new StatsServiceImpl(statsRepository);
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

        when(statsRepository.save(any())).thenReturn(stats);

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
                .hits(2L)
                .build();
        StatsCountDto count = StatsCountDto.builder()
                .uri("/test")
                .app("app-test")
                .hits(2L)
                .build();
        Stats stats = Stats.builder()
                .app("app-test")
                .uri("/test")
                .ip("0.0.0.0")
                .times(LocalDateTime.now())
                .build();

        when(statsRepository.findByTimesBetween(any(), any())).thenReturn(List.of(stats));

        List<Stats> responseCurrent = statsRepository
                .findByTimesBetween(LocalDateTime.of(2000, 3, 1, 1, 1),
                        LocalDateTime.of(2025, 3, 1, 1, 1));

        assert (responseCurrent.contains(stats));
    }
}