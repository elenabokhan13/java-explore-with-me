package ru.practicum.validator;

import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Status;
import ru.practicum.request.storage.RequestRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        Map<Long, ParticipationRequest> requestsResponse;
        if (updateEvent.getStatus() == Status.REJECTED) {
            requestsResponse = setRejected(requestRepository, requests, updateEvent);
        } else {
            requestsResponse = setConfirmed(requestRepository, requests, updateEvent,
                    event, confirmedNumber);
        }
        return requestsResponse;
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

    private static ParticipationRequest validateRequestPending(ParticipationRequest currentRequest) {
        if (currentRequest.getStatus() != Status.PENDING) {
            throw new ServerErrorException("Request must have status PENDING");
        }
        return currentRequest;
    }

    private static Map<Long, ParticipationRequest> setRejected(RequestRepository requestRepository,
                                                               Map<Long, ParticipationRequest> requests,
                                                               EventRequestStatusUpdateRequest updateEvent) {
        List<ParticipationRequest> requestsToSave = new ArrayList<>();
        for (Long requestId : updateEvent.getRequestIds()) {
            ParticipationRequest currentRequest = validateRequestPending(requests.get(requestId));
            currentRequest.setStatus(Status.REJECTED);
            requests.put(currentRequest.getId(), currentRequest);
            requestsToSave.add(currentRequest);
        }
        requestRepository.saveAll(requestsToSave);
        return requests;
    }

    private static Map<Long, ParticipationRequest> setConfirmed(RequestRepository requestRepository,
                                                                Map<Long, ParticipationRequest> requests,
                                                                EventRequestStatusUpdateRequest updateEvent,
                                                                Event event, int confirmedNumber) {
        List<ParticipationRequest> requestsToSave = new ArrayList<>();
        for (Long requestId : updateEvent.getRequestIds()) {
            ParticipationRequest currentRequest = validateRequestPending(requests.get(requestId));
            if ((confirmedNumber + 1) > event.getParticipantLimit()) {
                for (ParticipationRequest request : requests.values()) {
                    if (request.getStatus() == Status.PENDING) {
                        request.setStatus(Status.REJECTED);
                        requests.put(request.getId(), request);
                        requestsToSave.add(request);
                    }
                }
                throw new ServerErrorException("The participant limit has been reached");
            }
            currentRequest.setStatus(Status.CONFIRMED);
            requests.put(currentRequest.getId(), currentRequest);
            requestsToSave.add(currentRequest);
        }
        requestRepository.saveAll(requestsToSave);
        return requests;
    }
}
