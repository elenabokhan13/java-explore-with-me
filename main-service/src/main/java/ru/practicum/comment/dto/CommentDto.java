package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CommentDto {

    private Long id;

    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

    @NotBlank
    @Size(min = 1, max = 64, message = "The comment length should be between 1 and 64 symbols")
    private String comment;

    @Builder.Default
    private LocalDateTime commentPostingTime = LocalDateTime.now();

    @Builder.Default
    private Boolean edited = false;
}
