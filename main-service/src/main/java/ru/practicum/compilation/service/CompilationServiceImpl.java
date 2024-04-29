package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationEvent;
import ru.practicum.compilation.storage.CompilationEventRepository;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.AccessForbiddenError;
import ru.practicum.exception.InvalidRequestException;
import ru.practicum.validator.CompilationValidator;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    @Autowired
    private final CompilationRepository compilationRepository;

    @Autowired
    private final EventRepository eventRepository;

    @Autowired
    private final CompilationEventRepository compilationEventRepository;

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Page<Compilation> response;
        if (pinned) {
            response = compilationRepository.findByPinnedTrue(pageable);
        } else {
            response = compilationRepository.findByPinnedFalse(pageable);
        }

        List<CompilationDto> responseFinal = new ArrayList<>();

        for (Compilation current : response) {
            CompilationDto compilation = CompilationMapper.compilationToDto(current);
            List<CompilationEvent> events = compilationEventRepository.getByCompilationId(current.getId());
            List<EventShortDto> eventsDto = new ArrayList<>();
            for (CompilationEvent event : events) {
                eventsDto.add(EventMapper.eventToEventShortDto(eventRepository.findById(event.getEventId()).get()));
            }
            compilation.setEvents(eventsDto);
            responseFinal.add(compilation);
        }

        return responseFinal;
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation response = CompilationValidator.validateCompilationExists(compilationRepository, compId);
        CompilationDto compilation = CompilationMapper.compilationToDto(response);
        List<CompilationEvent> events = compilationEventRepository.getByCompilationId(compId);
        List<EventShortDto> eventsDto = new ArrayList<>();
        for (CompilationEvent event : events) {
            eventsDto.add(EventMapper.eventToEventShortDto(eventRepository.findById(event.getEventId()).get()));
        }
        compilation.setEvents(eventsDto);
        return compilation;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilation) {
        Compilation current = compilationRepository.findByTitle(compilation.getTitle());
        if (current != null) {
            throw new AccessForbiddenError("Compilation with such title already exists");
        }

        Compilation compilationForSave = Compilation.builder()
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .build();
        Compilation compilationSaved = compilationRepository.save(compilationForSave);
        if (compilation.getEvents() != null) {
            for (Long eventId : compilation.getEvents()) {
                compilationEventRepository.save(CompilationEvent.builder()
                        .compilationId(compilationSaved.getId())
                        .eventId(eventId)
                        .build());
            }
        }
        CompilationDto response = CompilationMapper.compilationToDto(compilationSaved);
        addEvents(response);
        return response;
    }

    @Override
    public void deleteCompilation(Long compId) {
        CompilationValidator.validateCompilationExists(compilationRepository, compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilation) {
        Compilation compilationUpdated = CompilationValidator.validateCompilationExists(compilationRepository, compId);
        if (compilation.getTitle() != null) {
            if ((compilation.getTitle().length() >= 1) && (compilation.getTitle().length() <= 50)) {
                Compilation current = compilationRepository.findByTitle(compilation.getTitle());
                if (current != null) {
                    throw new AccessForbiddenError("Compilation with title " + compilation.getTitle() + " already exists");
                }
                compilationUpdated.setTitle(compilation.getTitle());
            } else {
                throw new InvalidRequestException("Compilation title should be between 1 and 50 characters");
            }
        }
        if (compilation.getPinned() != null) {
            compilationUpdated.setPinned(compilation.getPinned());
        }
        if (compilation.getEvents() != null) {
            compilationEventRepository.deleteByCompilationId(compilationUpdated.getId());
            for (Long eventId : compilation.getEvents()) {
                compilationEventRepository.save(CompilationEvent.builder()
                        .compilationId(compilationUpdated.getId())
                        .eventId(eventId)
                        .build());
            }
        }
        CompilationDto response = CompilationMapper.compilationToDto(compilationRepository.save(compilationUpdated));
        addEvents(response);

        return response;
    }

    private void addEvents(CompilationDto compilation) {
        List<Long> eventIds = compilationEventRepository.getByCompilationId(compilation.getId()).stream()
                .map(CompilationEvent::getEventId).collect(Collectors.toList());
        List<EventShortDto> events = new ArrayList<>();
        if (eventIds.size() > 0) {
            events = eventRepository.findByIdIn(eventIds).stream()
                    .map(EventMapper::eventToEventShortDto).collect(Collectors.toList());
        }
        compilation.setEvents(events);
    }
}
