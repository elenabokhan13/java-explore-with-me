package ru.practicum.validator;

import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.exception.AccessForbiddenError;
import ru.practicum.exception.ObjectNotFoundException;

public class CompilationValidator {
    public static Compilation validateCompilationExists(CompilationRepository compilationRepository, Long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Compilation with id="
                + id + " was not found"));
    }

    public static void isCompilationExists(CompilationRepository compilationRepository, String title) {
        Compilation current = compilationRepository.findByTitle(title);
        if (current != null) {
            throw new AccessForbiddenError("Compilation with title " + title + " already exists");
        }
    }

}
