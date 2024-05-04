package ru.practicum.stats.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    List<Stats> findByTimesBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<Stats> findByTimesBetween(LocalDateTime start, LocalDateTime end);


}
