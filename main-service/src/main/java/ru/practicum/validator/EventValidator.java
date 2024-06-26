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
        return eventRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Event by id=" + id + " not found"));
    }

    public static Event validateRequest(CategoryRepository categoryRepository, Event event, UpdateEventRequest request) {
        if (event.getState() == State.PUBLISHED) {
            throw new ServerErrorException("Event with id=" + event.getId() + " cannot be updated because its status is "
                    + event.getState());
        }

        if (request.getAnnotation() != null) {
            validateAnnotation(event, request.getAnnotation());
        }
        if (request.getCategory() != null) {
            validateCategory(event, categoryRepository, request.getCategory());
        }
        if (request.getDescription() != null) {
            validateDescription(event, request.getDescription());
        }
        if (request.getEventDate() != null) {
            validateEventDate(request.getEventDate(), event);
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            validateParticipationLimit(event, request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            validateTitle(event, request.getTitle());
        }
        return event;
    }

    public static void validateEventPublished(Event event) {
        if (event.getState() != State.PUBLISHED) {
            throw new ObjectNotFoundException("Event with id=" + event.getId() + " is not published yet and cannot be " +
                    "accessed from public API");
        }
    }

    private static void validateAnnotation(Event event, String title) {
        if ((title.length() >= 20) && (title.length() <= 2000)) {
            event.setAnnotation(title);
        } else {
            throw new InvalidRequestException("Annotation should be between 20 and 2000 characters");
        }
    }

    private static void validateCategory(Event event, CategoryRepository categoryRepository, Long categoryId) {
        event.setCategory(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + categoryId + " not found")));
    }

    private static void validateDescription(Event event, String description) {
        if ((description.length() >= 20) && (description.length() <= 7000)) {
            event.setDescription(description);
        } else {
            throw new InvalidRequestException("Description should be between 20 and 7000 characters");
        }
    }

    private static void validateEventDate(LocalDateTime eventDate, Event event) {
        if (eventDate.isAfter(LocalDateTime.now().plusHours(2))) {
            event.setEventDate(eventDate);
        } else {
            throw new InvalidRequestException("The new date is invalid");
        }
    }

    private static void validateParticipationLimit(Event event, Long limit) {
        if (limit < 0) {
            throw new InvalidRequestException("Participants limit cannot be negative");
        }
        event.setParticipantLimit(limit);
    }

    private static void validateTitle(Event event, String title) {
        if ((title.length() >= 3) && (title.length() <= 120)) {
            event.setTitle(title);
        } else {
            throw new InvalidRequestException("Title should be between 3 and 120 characters");
        }
    }
}
