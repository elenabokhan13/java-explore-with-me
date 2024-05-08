package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.constant.Constant;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private String description;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    private Long id;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private Long participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private State state;
    private String title;
    private Long views;
    private List<Comment> comments;
}
