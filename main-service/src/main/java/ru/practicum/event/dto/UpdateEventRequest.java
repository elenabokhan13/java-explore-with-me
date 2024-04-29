package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.constant.Constant;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.StateAction;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UpdateEventRequest {

    private String annotation;
    private Long category;
    private String description;

    @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
