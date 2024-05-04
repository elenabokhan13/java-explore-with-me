package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.constant.Constant;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventShortDto {
    @NotBlank(message = "Краткое описание события не может быть пустым")
    @NotEmpty(message = "Краткое описание события не может быть пустым")
    private String annotation;

    @NotNull
    private CategoryDto category;
    private int confirmedRequests;

    @NotNull
    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Long id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotBlank(message = "Заголовок события не может быть пустым")
    @NotEmpty(message = "Заголовок события не может быть пустым")
    private String title;
    private Long views;
}
