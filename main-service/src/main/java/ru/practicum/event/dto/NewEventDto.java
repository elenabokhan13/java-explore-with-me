package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.constant.Constant;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NewEventDto {

    @NotBlank(message = "Краткое описание события не может быть пустым")
    @NotEmpty(message = "Краткое описание события не может быть пустым")
    @Size(min = 20, max = 2000)
    @NotNull
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank(message = "Полное описание события не может быть пустым")
    @NotEmpty(message = "Полное описание события не может быть пустым")
    @Size(min = 20, max = 7000)
    @NotNull
    private String description;

    @NotNull
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    @Builder.Default
    private boolean paid = false;

    @Builder.Default
    private Long participantLimit = 0L;

    @Builder.Default
    private boolean requestModeration = true;

    @NotBlank(message = "Заголовок события не может быть пустым")
    @NotEmpty(message = "Заголовок события не может быть пустым")
    @Size(min = 3, max = 120)
    private String title;
}
