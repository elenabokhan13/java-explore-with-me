package ru.practicum.request.dto;

import ru.practicum.request.model.ParticipationRequest;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto participationRequestToDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent())
                .requester(participationRequest.getRequester())
                .status(participationRequest.getStatus())
                .build();
    }
}
