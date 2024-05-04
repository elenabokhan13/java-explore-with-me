package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NewCompilationDto {
    private List<Long> events;

    @Builder.Default
    private Boolean pinned = false;

    @NotBlank(message = "Заголовок подборки не может быть пустым")
    @NotEmpty(message = "Заголовок подборки не может быть пустым")
    @Size(min = 1, max = 50)
    private String title;
}
