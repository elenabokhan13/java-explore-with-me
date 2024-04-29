package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Status;
import ru.practicum.request.storage.RequestRepository;
import ru.practicum.user.storage.UserRepository;
import ru.practicum.validator.EventValidator;
import ru.practicum.validator.RequestValidator;
import ru.practicum.validator.UserValidator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    @Autowired
    private final RequestRepository requestRepository;

    @Autowired
    private final EventRepository eventRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId) {
        Event event = EventValidator.validateEventExists(eventRepository, eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ServerErrorException("User with id=" + userId + " doesn't have access to the event with id="
                    + eventId);
        }
        return requestRepository.findByEvent(eventId).stream()
                .map(ParticipationRequestMapper::participationRequestToDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateParticipationRequest(Long userId, Long eventId,
                                                                     EventRequestStatusUpdateRequest updateEvent) {
        Event event = EventValidator.validateEventExists(eventRepository, eventId);

        UserValidator.validateUserAccessToEvent(userId, event.getInitiator().getId());

        Map<Long, ParticipationRequest> requests = requestRepository.findByEvent(eventId)
                .stream().collect(Collectors.toMap(ParticipationRequest::getId, Function.identity()));

        Map<Long, ParticipationRequest> result = RequestValidator
                .validateUpdateRequest(requestRepository, requests, updateEvent, event);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest request : result.values()) {
            if (request.getStatus() == Status.CONFIRMED) {
                confirmedRequests.add(ParticipationRequestMapper.participationRequestToDto(request));
            } else if (request.getStatus() == Status.REJECTED) {
                rejectedRequests.add(ParticipationRequestMapper.participationRequestToDto(request));
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Override
    public Collection<ParticipationRequestDto> getUserParticipationRequests(Long userId) {
        UserValidator.validateUserExists(userRepository, userId);
        return requestRepository.findByRequester(userId).stream()
                .map(ParticipationRequestMapper::participationRequestToDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        UserValidator.validateUserExists(userRepository, userId);
        Event event = EventValidator.validateEventExists(eventRepository, eventId);
        ParticipationRequest request = requestRepository.findByEventAndRequester(eventId, userId);
        RequestValidator.validateNewRequest(requestRepository, request, event, userId, eventId);

        request = ParticipationRequest.builder()
                .event(eventId)
                .requester(userId)
                .status(Status.PENDING)
                .created(LocalDateTime.now())
                .build();

        if ((event.getParticipantLimit() == 0) || (!event.getRequestModeration())) {
            request.setStatus(Status.CONFIRMED);
        }
        return ParticipationRequestMapper.participationRequestToDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = RequestValidator.validateRequestExists(requestRepository, requestId);
        UserValidator.validateUserAccessToRequest(request.getRequester(), userId);
        if (request.getStatus() == Status.CONFIRMED) {
            throw new ServerErrorException("This request already been approved");
        }
        request.setStatus(Status.CANCELED);
        return ParticipationRequestMapper.participationRequestToDto(requestRepository.save(request));
    }
}
