package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationController {
    @Autowired
    private final CompilationService compilationService;

    @GetMapping(path = "/compilations")
    public Collection<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                      @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпойнту /compilations для получения списка подборок событий");
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping(path = "/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Получен запрос к эндпойнту /compilations для получения подборки событий по id {}", compId);
        return compilationService.getCompilation(compId);
    }

    @PostMapping(path = "/admin/compilations")
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto compilation) {
        log.info("Получен запрос к эндпойнту /admin/compilations для создания подборки событий");
        return new ResponseEntity<>(compilationService.createCompilation(compilation), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/admin/compilations/{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable Long compId) {
        log.info("Получен запрос к эндпойнту /admin/compilations для удаления подборки событий по id {}", compId);
        compilationService.deleteCompilation(compId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId, @RequestBody UpdateCompilationRequest compilation) {
        log.info("Получен запрос к эндпойнту /admin/compilations для обновления подборки событий по id {}", compId);
        return compilationService.updateCompilation(compId, compilation);
    }
}
