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
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.InvalidRequestException;
import ru.practicum.validator.CompilationValidator;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

        if (response.getSize() == 0) {
            return List.of();
        }

        return toCompilationDtoAndAddEvents(response.getContent());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation response = CompilationValidator.validateCompilationExists(compilationRepository, compId);

        return toCompilationDtoAndAddEvents(List.of(response)).get(0);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilation) {
        CompilationValidator.isCompilationExists(compilationRepository, compilation.getTitle());

        Compilation compilationForSave = Compilation.builder()
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .build();

        Compilation compilationSaved = compilationRepository.save(compilationForSave);

        if (compilation.getEvents() != null) {
            saveEvents(compilation.getEvents(), compilationSaved.getId());
        }
        return toCompilationDtoAndAddEvents(List.of(compilationSaved)).get(0);
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
                CompilationValidator.isCompilationExists(compilationRepository, compilation.getTitle());
                compilationUpdated.setTitle(compilation.getTitle());
            } else {
                throw new InvalidRequestException("Compilation title should be between 1 and 50 characters");
            }
        }
        if (compilation.getPinned() != null) {
            compilationUpdated.setPinned(compilation.getPinned());
        }
        Compilation compilationSaved = compilationRepository.save(compilationUpdated);
        if (compilation.getEvents() != null) {
            compilationEventRepository.deleteByCompilationId(compilationUpdated.getId());
            saveEvents(compilation.getEvents(), compilationSaved.getId());
        }

        return toCompilationDtoAndAddEvents(List.of(compilationSaved)).get(0);
    }

    private List<CompilationDto> toCompilationDtoAndAddEvents(List<Compilation> response) {
        List<CompilationEvent> compilationEvents = getCompilationEvents(response);
        if (compilationEvents.size() == 0) {
            return response.stream().map(CompilationMapper::compilationToDto).peek(x -> x.setEvents(List.of()))
                    .collect(Collectors.toList());
        }
        Map<Long, Event> events = mapEvents(compilationEvents);

        return addEvents(compilationEvents, response, events);
    }

    private void saveEvents(List<Long> eventIds, Long compilationId) {
        List<CompilationEvent> compilationEvents = new ArrayList<>();
        for (Long eventId : eventIds) {
            compilationEvents.add(CompilationEvent.builder()
                    .compilationId(compilationId)
                    .eventId(eventId)
                    .build());
        }
        compilationEventRepository.saveAll(compilationEvents);
    }

    private Map<Long, Event> mapEvents(List<CompilationEvent> compilationEvents) {
        List<Long> eventIds = compilationEvents.stream().map(CompilationEvent::getEventId).collect(Collectors.toList());

        return eventRepository.findByIdIn(eventIds).stream().collect(Collectors
                .toMap(Event::getId, Function.identity()));
    }

    private List<CompilationEvent> getCompilationEvents(List<Compilation> response) {
        List<Long> compilationIds = response.stream().map(Compilation::getId).collect(Collectors.toList());
        return compilationEventRepository.getByCompilationIdIn(compilationIds);
    }

    private List<CompilationDto> addEvents(List<CompilationEvent> compilationEvents, List<Compilation> response,
                                           Map<Long, Event> events) {
        List<CompilationDto> responseFinal = new ArrayList<>();

        Map<Long, List<Long>> compilationIdAndEventId = compilationEvents.stream()
                .collect(Collectors.groupingBy(CompilationEvent::getCompilationId,
                        Collectors.mapping(CompilationEvent::getEventId, Collectors.toList())));

        for (Compilation current : response) {
            CompilationDto compilation = CompilationMapper.compilationToDto(current);
            if (compilationIdAndEventId.get(compilation.getId()) != null) {
                List<EventShortDto> eventsDto = new ArrayList<>();
                for (Long id : compilationIdAndEventId.get(compilation.getId())) {
                    eventsDto.add(EventMapper.eventToEventShortDto(events.get(id)));
                }
                compilation.setEvents(eventsDto);
            } else {
                compilation.setEvents(List.of());
            }
            responseFinal.add(compilation);
        }
        return responseFinal;
    }
}
