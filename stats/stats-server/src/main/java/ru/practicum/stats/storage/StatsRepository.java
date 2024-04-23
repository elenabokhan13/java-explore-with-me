package ru.practicum.stats.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query(value = "select * from stats where times between ?1 and ?2 and uri in (?3)", nativeQuery = true)
    List<Stats> getByParametersWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select * from stats where times between ?1 and ?2", nativeQuery = true)
    List<Stats> getByParametersNoUris(LocalDateTime start, LocalDateTime end);

}
