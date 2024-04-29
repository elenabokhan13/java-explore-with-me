package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CompilationDto {
    private List<EventShortDto> events;

    @NotNull
    private Long id;

    @NotNull
    private Boolean pinned;

    @NotBlank
    @NotEmpty
    private String title;
}
