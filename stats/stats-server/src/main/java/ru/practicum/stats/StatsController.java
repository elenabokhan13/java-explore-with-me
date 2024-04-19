package ru.practicum.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.service.StatsService;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping(path = "/hit")
    public StatsDto createHit(@RequestBody StatsDto statsDto) {
        log.info("Получен запрос к эндпойнту /hit для записи обращения к эндпойнту " + statsDto.getUri()
                + " от приложения " + statsDto.getApp());
        return statsService.createHit(statsDto);
    }

    @GetMapping(path = "/stats")
    public List<StatsCountDto> getStats(@RequestParam String start, @RequestParam String end,
                                        @RequestParam(defaultValue = "") List<String> uris,
                                        @RequestParam(defaultValue = "false") String unique) {
        log.info("Получен запрос к эндпойнту /stats для получения статистики обращений");
        return statsService.getStats(start, end, uris, unique);
    }
}
