package ru.practicum.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatsDto {
    @NotBlank(message = "Введите название приложения(app)")
    @NotEmpty(message = "Введите название приложения(app)")
    private String app;

    @NotBlank(message = "Введите адрес запроса")
    @NotEmpty(message = "Введите адрес запроса")
    private String uri;

    @NotNull(message = "Укажите ip пользователя")
    private String ip;
    private LocalDateTime times;
}
