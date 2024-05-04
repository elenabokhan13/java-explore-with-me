package ru.practicum.compilation.dto;

import ru.practicum.compilation.model.Compilation;

public class CompilationMapper {

    public static CompilationDto compilationToDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
