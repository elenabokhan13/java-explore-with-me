package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long compId);

    CompilationDto createCompilation(NewCompilationDto compilation);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilation);
}
