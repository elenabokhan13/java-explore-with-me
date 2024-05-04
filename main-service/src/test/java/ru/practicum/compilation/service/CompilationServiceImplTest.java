package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.storage.CompilationEventRepository;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.ObjectNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql("/schema.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CompilationServiceImplTest {

    private CompilationService compilationService;

    @Autowired
    CompilationRepository compilationRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    CompilationEventRepository compilationEventRepository;

    @BeforeEach
    public void setUp() {
        compilationService = new CompilationServiceImpl(compilationRepository, eventRepository,
                compilationEventRepository);
    }

    @Test
    void getCompilation() {
        NewCompilationDto newCompilationDto = NewCompilationDto.builder()
                .title("title")
                .build();

        compilationService.createCompilation(newCompilationDto);
        CompilationDto compilationDto = compilationService.getCompilation(1L);

        assertThat(compilationDto.getId().equals(1L));
        assertThat(compilationDto.getTitle().equals("title"));
    }

    @Test
    void createCompilationEmptyEvents() {
        NewCompilationDto newCompilationDto = NewCompilationDto.builder()
                .title("title")
                .build();

        CompilationDto compilationDto = compilationService.createCompilation(newCompilationDto);

        assertThat(compilationDto.getId().equals(1L));
        assertThat(compilationDto.getTitle().equals("title"));
    }

    @Test
    void deleteCompilationNotPresent() {
        assertThrows(ObjectNotFoundException.class, () -> {
            compilationService.deleteCompilation(1L);
        });
    }

    @Test
    void updateCompilation() {
        NewCompilationDto newCompilationDto = NewCompilationDto.builder()
                .title("title")
                .build();

        compilationService.createCompilation(newCompilationDto);

        UpdateCompilationRequest request = UpdateCompilationRequest.builder()
                .pinned(true)
                .build();

        CompilationDto compilationDto = compilationService.updateCompilation(1L, request);

        assertThat(compilationDto.getId().equals(1L));
        assertThat(compilationDto.getTitle().equals("title"));
        assertThat(compilationDto.getPinned().equals(true));
    }
}