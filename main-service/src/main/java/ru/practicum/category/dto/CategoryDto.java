package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Название категории не может быть пустым")
    @NotEmpty(message = "Название категории не может быть пустым")
    @Size(min = 1, max = 50)
    private String name;
}
