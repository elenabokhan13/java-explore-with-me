package ru.practicum.validator;

import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.InvalidRequestException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ServerErrorException;

import java.time.LocalDateTime;

public class EventValidator {
    public static Event validateEventExists(EventRepository eventRepository, Long id) {
        return eventRepository.findById(id).
                orElseThrow(() -> new ObjectNotFoundException("Event by id=" + id + " not found"));
    }

    public static Event validateRequest(CategoryRepository categoryRepository, Event event, UpdateEventRequest request) {
        if (event.getState() == State.PUBLISHED) {
            throw new ServerErrorException("Event with id=" + event.getId() + " cannot be updated because its status is "
                    + event.getState());
        }
        if (request.getAnnotation() != null) {
            if ((request.getAnnotation().length() >= 20) & (request.getAnnotation().length() <= 2000)) {
                event.setAnnotation(request.getAnnotation());
            } else {
                throw new InvalidRequestException("Annotation should be between 20 and 2000 characters");
            }
        }
        if (request.getCategory() != null) {
            event.setCategory(categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + request.getCategory() + " not found")));
        }
        if (request.getDescription() != null) {
            if ((request.getDescription().length() >= 20) & (request.getDescription().length() <= 7000)) {
                event.setDescription(request.getDescription());
            } else {
                throw new InvalidRequestException("Description should be between 20 and 7000 characters");
            }
        }
        if (request.getEventDate() != null) {
            if (request.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                event.setEventDate(request.getEventDate());
            } else {
                throw new InvalidRequestException("The new date is invalid");
            }
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            if (request.getParticipantLimit() < 0) {
                throw new InvalidRequestException("Participants limit cannot be negative");
            }
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            if ((request.getTitle().length() >= 3) & (request.getTitle().length() <= 120)) {
                event.setTitle(request.getTitle());
            } else {
                throw new InvalidRequestException("Title should be between 3 and 120 characters");
            }
        }
        return event;
    }
}
