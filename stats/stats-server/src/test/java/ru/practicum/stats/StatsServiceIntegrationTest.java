package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.stats.service.StatsServiceImpl;
import ru.practicum.stats.storage.StatsRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/schema.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceIntegrationTest {
    private StatsServiceImpl statsService;

    @Autowired
    StatsRepository statsRepository;
    private StatsDto statsDtoOne;
    private StatsDto statsDtoTwo;
    private StatsDto statsDtoThree;
    private StatsCountDto statsCountDtoOne;
    private StatsCountDto statsCountDtoTwo;
    private StatsCountDto statsCountDtoThree;

    @BeforeEach
    public void createMeta() {
        statsService = new StatsServiceImpl(statsRepository);
        statsDtoOne = StatsDto.builder()
                .app("app-test")
                .uri("/test")
                .ip("0.0.0.0")
                .build();
        statsDtoTwo = StatsDto.builder()
                .app("app-test")
                .uri("/test/1")
                .ip("0.0.0.0")
                .build();
        statsDtoThree = StatsDto.builder()
                .app("app-test")
                .uri("/test/1")
                .ip("0.0.0.0")
                .build();
        statsCountDtoOne = StatsCountDto.builder()
                .app("app-test")
                .uri("/test/1")
                .hits(2)
                .build();
        statsCountDtoTwo = StatsCountDto.builder()
                .app("app-test")
                .uri("/test/1")
                .hits(1)
                .build();
        statsCountDtoThree = StatsCountDto.builder()
                .app("app-test")
                .uri("/test")
                .hits(1)
                .build();

        statsService.createHit(statsDtoOne);
        statsService.createHit(statsDtoTwo);
        statsService.createHit(statsDtoThree);
    }

    @Test
    public void getStatsWithUriUniqueFalseTest() {
        List<StatsCountDto> response = statsService.getStats("2000-03-01 00:00:00", "2025-01-01 00:00:00",
                List.of("/test/1"), "false");

        assertThat(response.contains(statsCountDtoOne));
    }

    @Test
    public void getStatsWithUriUniqueTrueTest() {
        List<StatsCountDto> response = statsService.getStats("2000-03-01 00:00:00", "2025-01-01 00:00:00",
                List.of("/test/1"), "true");

        assertThat(response.contains(statsCountDtoThree));
    }

    @Test
    public void getStatsNoUriUniqueFalseTest() {
        List<StatsCountDto> response = statsService.getStats("2000-03-01 00:00:00", "2025-01-01 00:00:00",
                List.of(), "false");

        assertThat(response.contains(statsCountDtoOne));
        assertThat(response.contains(statsCountDtoTwo));
    }
}
