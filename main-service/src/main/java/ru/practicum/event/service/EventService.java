package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    Collection<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest request);

    Collection<EventShortDto> getEventsByFilters(String text, List<Long> categories, Boolean paid,
                                                 String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                 String sort, int from, int size, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    Collection<EventFullDto> getEventsByAdminFilters(List<Long> users, List<String> states, List<Long> categories,
                                                     String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventRequest event);
}
