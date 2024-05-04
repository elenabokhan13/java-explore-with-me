package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.Collection;

public interface RequestService {
    Collection<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateParticipationRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest event);

    Collection<ParticipationRequestDto> getUserParticipationRequests(Long userId);

    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);
}
