package ru.practicum.validator;

import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Status;
import ru.practicum.request.storage.RequestRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class RequestValidator {
    public static ParticipationRequest validateRequestExists(RequestRepository requestRepository, Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Request with id=" + id + " was not found"));
    }

    public static Map<Long, ParticipationRequest> validateUpdateRequest(RequestRepository requestRepository,
                                                                        Map<Long, ParticipationRequest> requests,
                                                                        EventRequestStatusUpdateRequest updateEvent,
                                                                        Event event) {
        int confirmedNumber = 0;
        for (ParticipationRequest request : requests.values()) {
            if (request.getStatus() == Status.CONFIRMED) {
                confirmedNumber += 1;
            }
        }
        if (updateEvent.getStatus() == Status.REJECTED) {
            for (Long requestId : updateEvent.getRequestIds()) {
                ParticipationRequest currentRequest = requests.get(requestId);
                if (currentRequest.getStatus() != Status.PENDING) {
                    throw new ServerErrorException("Request must have status PENDING");
                }
                currentRequest.setStatus(Status.REJECTED);
                requests.put(currentRequest.getId(), currentRequest);
                requestRepository.save(currentRequest);
            }
        } else {
            for (Long requestId : updateEvent.getRequestIds()) {
                ParticipationRequest currentRequest = requests.get(requestId);
                if (currentRequest.getStatus() != Status.PENDING) {
                    throw new ServerErrorException("Request must have status PENDING");
                }
                if ((confirmedNumber + 1) > event.getParticipantLimit()) {
                    for (ParticipationRequest request : requests.values()) {
                        if (request.getStatus() == Status.PENDING) {
                            request.setStatus(Status.REJECTED);
                            requests.put(request.getId(), request);
                            requestRepository.save(request);
                        }
                    }
                    throw new ServerErrorException("The participant limit has been reached");
                }
                currentRequest.setStatus(Status.CONFIRMED);
                requests.put(currentRequest.getId(), currentRequest);
                requestRepository.save(currentRequest);
            }
        }
        return requests;
    }

    public static void validateNewRequest(RequestRepository requestRepository, ParticipationRequest request,
                                          Event event, Long userId, Long eventId) {
        if (request != null) {
            throw new ServerErrorException("The request cannot be sent more than one time");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ServerErrorException("The event initiator cannot send request to participate in it");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ServerErrorException("Participation request cannot be created for unpublished event");
        }
        Collection<ParticipationRequest> requests = requestRepository.findByEventAndStatus(eventId, Status.CONFIRMED);
        if ((requests.size() == event.getParticipantLimit()) && (event.getParticipantLimit() != 0)) {
            throw new ServerErrorException("The event reached limit of participation requests");
        }
    }
}
