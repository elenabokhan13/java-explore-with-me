package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserShortDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @NotEmpty(message = "Имя не может быть пустым")
    private String name;
}
