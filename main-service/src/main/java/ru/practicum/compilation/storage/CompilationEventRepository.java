package ru.practicum.compilation.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.CompilationEvent;

import java.util.List;

@Repository
public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    List<CompilationEvent> getByCompilationIdIn(List<Long> compilationId);

    void deleteByCompilationId(Long id);
}
