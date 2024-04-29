package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.request.model.Status;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private Status status;
}
