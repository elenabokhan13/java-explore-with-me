package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.exception.InvalidRequestException;
import ru.practicum.stats.StatsCountDto;
import ru.practicum.stats.StatsDto;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.model.StatsMapper;
import ru.practicum.stats.storage.StatsRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    @Autowired
    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public StatsDto createHit(StatsDto statsDto) {
        statsDto.setTimes(LocalDateTime.now());
        return StatsMapper.statsToDto(statsRepository.save(StatsMapper.dtoToStats(statsDto)));
    }

    @Override
    public List<StatsCountDto> getStats(String start, String end, List<String> uris, String unique) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        try {
            startDate = LocalDateTime.parse(start);
            endDate = LocalDateTime.parse(end);
        } catch (Exception e) {
            throw new InvalidRequestException("Дата начала и/или окончания выборки неверного формата");
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidRequestException("Дата начала выборки не может быть после даты окончания выборки");
        }

        List<StatsCountDto> response = new ArrayList<>();
        List<Stats> statsData;

        if (uris.size() == 0) {
            statsData = statsRepository.findByTimesBetween(startDate, endDate);
        } else {
            statsData = statsRepository.findByTimesBetweenAndUriIn(startDate, endDate, uris);
        }
        Map<String, Map<String, Integer>> statsDataProcessed = new HashMap<>();

        if (Objects.equals(unique, "true")) {
            Map<String, Map<String, Set<String>>> statsDataProcessedIp = new HashMap<>();

            for (Stats data : statsData) {
                Map<String, Integer> dataUri = statsDataProcessed.get(data.getUri());
                if (dataUri == null) {
                    Map<String, Integer> currentDataUri = new HashMap<>();
                    currentDataUri.put(data.getApp(), 1);
                    statsDataProcessed.put(data.getUri(), currentDataUri);

                    Map<String, Set<String>> currentDataUriIp = new HashMap<>();
                    Set<String> currentIp = new HashSet<>();
                    currentIp.add(data.getIp());
                    currentDataUriIp.put(data.getApp(), currentIp);
                    statsDataProcessedIp.put(data.getUri(), currentDataUriIp);
                } else {
                    Integer count = dataUri.get(data.getApp());
                    if (count == null) {
                        dataUri.put(data.getApp(), 1);
                        statsDataProcessed.put(data.getUri(), dataUri);

                        Map<String, Set<String>> currentDataUriIp = statsDataProcessedIp.get(data.getUri());
                        Set<String> currentIp = new HashSet<>();
                        currentIp.add(data.getIp());
                        currentDataUriIp.put(data.getApp(), currentIp);
                        statsDataProcessedIp.put(data.getUri(), currentDataUriIp);
                    } else {
                        if (!statsDataProcessedIp.get(data.getUri()).get(data.getApp()).contains(data.getIp())) {
                            Set<String> currentIp = statsDataProcessedIp.get(data.getUri()).get(data.getApp());
                            currentIp.add(data.getIp());
                            statsDataProcessedIp.get(data.getUri()).put(data.getApp(), currentIp);

                            dataUri.put(data.getApp(), count + 1);
                            statsDataProcessed.put(data.getUri(), dataUri);
                        }
                    }
                }
            }
        } else {

            for (Stats data : statsData) {
                Map<String, Integer> dataUri = statsDataProcessed.get(data.getUri());
                if (dataUri == null) {
                    Map<String, Integer> currentDataUri = new HashMap<>();
                    currentDataUri.put(data.getApp(), 1);
                    statsDataProcessed.put(data.getUri(), currentDataUri);
                } else {
                    Integer count = dataUri.get(data.getApp());
                    if (count == null) {
                        dataUri.put(data.getApp(), 1);
                        statsDataProcessed.put(data.getUri(), dataUri);
                    } else {
                        dataUri.put(data.getApp(), count + 1);
                        statsDataProcessed.put(data.getUri(), dataUri);
                    }
                }
            }
        }

        for (String currentUri : statsDataProcessed.keySet()) {
            for (String currentApp : statsDataProcessed.get(currentUri).keySet()) {
                response.add(StatsCountDto.builder()
                        .uri(currentUri)
                        .app(currentApp)
                        .hits(Long.valueOf(statsDataProcessed.get(currentUri).get(currentApp)))
                        .build());
            }
        }

        return response.stream().sorted(Comparator.comparing(StatsCountDto::getHits).reversed()).collect(Collectors.toList());
    }
}
