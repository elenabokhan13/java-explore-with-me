package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250)
    private String name;

    @Email(message = "Введите верный имейл")
    @NotBlank(message = "Введите верный имейл")
    @NotEmpty(message = "Введите верный имейл")
    @Size(min = 6, max = 254)
    private String email;
}
