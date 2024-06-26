package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.InvalidRequestException;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.request.model.Status;
import ru.practicum.request.storage.RequestRepository;
import ru.practicum.stats.StatsCountDto;
import ru.practicum.stats.StatsDto;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;
import ru.practicum.validator.CategoryValidator;
import ru.practicum.validator.EventValidator;
import ru.practicum.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.constant.Constant.DATE_TIME_FORMAT;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    @Autowired
    private final EventRepository eventRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CategoryRepository categoryRepository;

    @Autowired
    private final RequestRepository requestRepository;

    private final StatsClient statsClient;


    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getParticipantLimit() < 0) {
            throw new InvalidRequestException("Participants limit cannot be negative");
        }
        User user = UserValidator.validateUserExists(userRepository, userId);
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("The date should be at least two hours ahead");
        }
        Event event = EventMapper.eventFromNewEventDto(newEventDto);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        event.setCategory(CategoryValidator.validateCategoryExists(categoryRepository, newEventDto.getCategory()));
        return EventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public Collection<EventShortDto> getEvents(Long userId, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByInitiator(userId, pageable);
        return getEventShortDtoWithViews(events.getContent());
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        UserValidator.validateUserExists(userRepository, userId);
        Event event = EventValidator.validateEventExists(eventRepository, eventId);
        UserValidator.validateUserAccessToEvent(event.getInitiator().getId(), userId);
        return getEventFullDtoWithViews(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest request) {
        UserValidator.validateUserExists(userRepository, userId);
        Event event = EventValidator.validateEventExists(eventRepository, eventId);
        UserValidator.validateUserAccessToEvent(event.getInitiator().getId(), userId);
        Event eventValidated = EventValidator.validateRequest(categoryRepository, event, request);

        if (request.getStateAction() != null) {
            if (request.getStateAction() == StateAction.SEND_TO_REVIEW) {
                eventValidated.setState(State.PENDING);
            } else {
                eventValidated.setState(State.CANCELED);
            }
        }
        return getEventFullDtoWithViews(event);
    }

    @Override
    public Collection<EventShortDto> getEventsByFilters(String text, List<Long> categories, Boolean paid,
                                                        String rangeStart, String rangeEnd,
                                                        Boolean onlyAvailable, String sort, int from, int size,
                                                        HttpServletRequest request) {

        List<Specification<Event>> specifications;

        if (rangeStart == null) {
            specifications = searchFilterToSpecifications(text, categories, paid, null,
                    null, onlyAvailable);

        } else {
            specifications = searchFilterToSpecifications(text, categories, paid,
                    LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), onlyAvailable);
        }

        List<EventShortDto> response = getEventsWithFilters(from, size, specifications, request);
        if (sort != null) {
            if (sort.equals("VIEWS")) {
                response.sort(Comparator.comparing(EventShortDto::getViews));
            } else {
                response.sort(Comparator.comparing(EventShortDto::getEventDate).reversed());
            }
        }
        return response;
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = EventValidator.validateEventExists(eventRepository, eventId);
        EventValidator.validateEventPublished(event);
        EventFullDto response = getEventFullDtoWithViews(event);

        statsClient.postStats(StatsDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build());
        return response;
    }

    @Override
    public Collection<EventFullDto> getEventsByAdminFilters(List<Long> users, List<String> states, List<Long> categories,
                                                            String rangeStart, String rangeEnd, int from, int size) {
        List<Specification<Event>> specifications;

        if (rangeStart == null) {
            specifications = searchAdminFilterToSpecifications(users, states, categories, null, null);

        } else {
            specifications = searchAdminFilterToSpecifications(users, states, categories,
                    LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        Page<Event> events = getEventsForPage(from, size, specifications);
        List<EventFullDto> response = events.stream().map(EventMapper::eventToEventFullDto)
                .peek(x -> x.setConfirmedRequests(requestRepository.findByEventAndStatus(x.getId(),
                        Status.CONFIRMED).size()))
                .collect(Collectors.toList());

        List<StatsCountDto> counts = getCountForEventFullDto(response);
        if (Objects.requireNonNull(counts).size() == 0) {
            return response;
        }
        Map<String, StatsCountDto> countsWithKeyUri = counts.stream().collect(Collectors.toMap(StatsCountDto::getUri,
                Function.identity()));

        for (int i = 0; i < response.size(); i++) {
            EventFullDto currentResponse = response.get(i);
            currentResponse.setViews(countsWithKeyUri.getOrDefault("/events/" + currentResponse.getId(),
                    StatsCountDto.builder().hits(0L).build()).getHits());
            response.set(i, currentResponse);
        }

        return response;
    }

    @Override
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventRequest request) {
        Event event = EventValidator.validateEventExists(eventRepository, eventId);

        Event eventValidated = EventValidator.validateRequest(categoryRepository, event, request);

        if (request.getStateAction() != null) {
            if (request.getStateAction() == StateAction.PUBLISH_EVENT) {
                if (eventValidated.getState() == State.CANCELED) {
                    throw new ServerErrorException("This event cannot be published because it is cancelled");
                }
                eventValidated.setState(State.PUBLISHED);
                eventValidated.setPublishedOn(LocalDateTime.now());
            } else {
                eventValidated.setState(State.CANCELED);
            }
        }
        return getEventFullDtoWithViews(event);
    }

    private List<StatsCountDto> getCountForEventFullDto(List<EventFullDto> response) {
        List<String> uris = new ArrayList<>();
        for (EventFullDto event : response) {
            uris.add("/events/" + event.getId());
        }
        return statsClient.getStats(LocalDateTime.now().minusMonths(12)
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                uris, "true").getBody();
    }

    private Page<Event> getEventsForPage(int from, int size, List<Specification<Event>> specifications) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findAll(
                specifications.stream().reduce(Specification::and).orElse(null), pageable
        );
    }

    private List<EventShortDto> getEventsWithFilters(int from, int size, List<Specification<Event>> specifications,
                                                     HttpServletRequest request) {
        Page<Event> events = getEventsForPage(from, size, specifications);
        List<EventShortDto> response = getEventShortDtoWithViews(events.getContent());

        statsClient.postStats(StatsDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build());

        return response;
    }

    private EventFullDto getEventFullDtoWithViews(Event event) {
        EventFullDto response = EventMapper.eventToEventFullDto(event);
        response.setConfirmedRequests(requestRepository.findByEventAndStatus(response.getId(),
                Status.CONFIRMED).size());

        List<StatsCountDto> views = Objects.requireNonNull(statsClient.getStats(LocalDateTime.now()
                        .minusMonths(12).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                LocalDateTime.now().plusMonths(12).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                List.of("/events/" + event.getId()), "true").getBody());
        if (views.size() > 0) {
            response.setViews(views.get(0).getHits());
        } else {
            response.setViews(0L);
        }
        return response;
    }

    private List<EventShortDto> getEventShortDtoWithViews(List<Event> events) {
        List<EventShortDto> response = events.stream().map(EventMapper::eventToEventShortDto)
                .peek(x -> x.setConfirmedRequests(requestRepository.findByEventAndStatus(x.getId(),
                        Status.CONFIRMED).size()))
                .collect(Collectors.toList());

        List<String> uris = new ArrayList<>();
        for (EventShortDto event : response) {
            uris.add("/events/" + event.getId());
        }
        List<StatsCountDto> counts = statsClient.getStats(LocalDateTime.now().minusMonths(12)
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)), uris, "true").getBody();

        if (Objects.requireNonNull(counts).size() == 0) {
            return response;
        }
        Map<String, StatsCountDto> countsWithKeyUri = counts.stream().collect(Collectors.toMap(StatsCountDto::getUri,
                Function.identity()));

        for (int i = 0; i < response.size(); i++) {
            EventShortDto currentResponse = response.get(i);
            currentResponse.setViews(countsWithKeyUri.getOrDefault("/events/" + currentResponse.getId(),
                    StatsCountDto.builder().hits(0L).build()).getHits());
            response.set(i, currentResponse);
        }

        return response;
    }


    private List<Specification<Event>> searchFilterToSpecifications(String text, List<Long> categories, Boolean paid,
                                                                    @Nullable LocalDateTime rangeStart,
                                                                    @Nullable LocalDateTime rangeEnd,
                                                                    Boolean onlyAvailable) {
        List<Specification<Event>> specifications = new ArrayList<>();

        specifications.add(text == null ? null : annotationOrDescriptionContaining(text));
        if (paid != null) {
            if (paid) {
                specifications.add(paidTrue());
            } else {
                specifications.add(paidFalse());
            }
        }
        specificationAddTimeAndCategories(specifications, rangeStart, rangeEnd, categories);
        specifications.add((!Boolean.TRUE.equals(onlyAvailable)) ? null : onlyAvailable());
        specifications.add(onlyPublished());

        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<Specification<Event>> searchAdminFilterToSpecifications(List<Long> users, List<String> states,
                                                                         List<Long> categories,
                                                                         @Nullable LocalDateTime rangeStart,
                                                                         @Nullable LocalDateTime rangeEnd) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specificationAddTimeAndCategories(specifications, rangeStart, rangeEnd, categories);
        if (users != null) {
            List<User> usersList = userRepository.findAllById(users);
            if (usersList.size() != users.size()) {
                throw new InvalidRequestException("Invalid users ids");
            }
            specifications.add(inUsers(usersList));
        }

        if (states != null) {
            List<State> statesList = states.stream().map(State::valueOf).collect(Collectors.toList());
            specifications.add(inStates(statesList));
        }

        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void specificationAddTimeAndCategories(List<Specification<Event>> specifications,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   List<Long> categories) {
        specifications.add((rangeStart == null)
                & (rangeEnd == null) ? rangeAfterNow(LocalDateTime.now()) : rangeBetween(rangeStart, rangeEnd));

        if (categories != null) {
            List<Category> categoryList = categoryRepository.findAllById(categories);
            if (categories.size() != categoryList.size()) {
                throw new InvalidRequestException("Invalid categories ids");
            }
            specifications.add(categoryIn(categoryList));
        }
    }

    private Specification<Event> annotationOrDescriptionContaining(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.like(root.get("annotation"),
                "%" + text + "%"), criteriaBuilder.like(root.get("description"), "%" + text + "%"));
    }

    private Specification<Event> paidTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("paid"));
    }

    private Specification<Event> paidFalse() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("paid"));
    }

    private Specification<Event> categoryIn(List<Category> categoryList) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category")).value(categoryList);
    }

    private Specification<Event> rangeBetween(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("eventDate"), rangeStart, rangeEnd);
    }

    private Specification<Event> rangeAfterNow(LocalDateTime rangeStart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("eventDate"), rangeStart);
    }

    private Specification<Event> onlyAvailable() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("participantLimit"),
                requestRepository.findByEventAndStatus(Long.valueOf(root.get("id").toString()), Status.CONFIRMED).size());
    }

    private Specification<Event> onlyPublished() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), State.PUBLISHED);
    }

    private Specification<Event> inUsers(List<User> users) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator")).value(users);
    }

    private Specification<Event> inStates(List<State> states) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states);
    }
}
