package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constant.Constant.USER_EVENT_URL;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventController {
    @Autowired
    private final EventService eventService;

    @PostMapping(path = USER_EVENT_URL)
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен запрос к эндпойнту /users/{}/events для создания события", userId);
        return new ResponseEntity<>(eventService.createEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping(path = USER_EVENT_URL)
    public Collection<EventShortDto> getEvents(@PathVariable Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпойнту /users/{}/events для получения списка событий пользователя", userId);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping(path = USER_EVENT_URL + "/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получен запрос к эндпойнту /users/{}}/events/{} для получения события", userId, eventId);
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping(path = USER_EVENT_URL + "/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @Valid @RequestBody UpdateEventRequest request) {
        log.info("Получен запрос к эндпойнту /users/{}/events/{} для обновления события", userId, eventId);
        return eventService.updateEvent(userId, eventId, request);
    }

    @GetMapping(path = "/events")
    public Collection<EventShortDto> getEventsByFilters(@RequestParam @Nullable String text,
                                                        @RequestParam @Nullable List<Long> categories,
                                                        @RequestParam @Nullable Boolean paid,
                                                        @RequestParam @Nullable String rangeStart,
                                                        @RequestParam @Nullable String rangeEnd,
                                                        @RequestParam @Nullable Boolean onlyAvailable,
                                                        @RequestParam @Nullable String sort,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        log.info("Получен запрос к эндпойнту /events для получения списка событий по фильтрам");
        return eventService.getEventsByFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size, request);
    }

    @GetMapping(path = "/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Получен запрос к эндпойнту /events для получения списка событий по фильтрам");
        return eventService.getEventById(eventId, request);
    }

    @GetMapping(path = "/admin/events")
    public Collection<EventFullDto> getEventsByAdminFilters(@RequestParam @Nullable List<Long> users,
                                                            @RequestParam @Nullable List<String> states,
                                                            @RequestParam @Nullable List<Long> categories,
                                                            @RequestParam @Nullable String rangeStart,
                                                            @RequestParam @Nullable String rangeEnd,
                                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпойнту /admin/events для получения списка событий по фильтрам");
        return eventService.getEventsByAdminFilters(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(path = "/admin/events/{eventId}")
    public EventFullDto updateAdminEvent(@PathVariable Long eventId, @RequestBody UpdateEventRequest event) {
        log.info("Получен запрос к эндпойнту /admin/events для обновления события {}", eventId);
        return eventService.updateAdminEvent(eventId, event);
    }
}
