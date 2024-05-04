package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Location;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.request.storage.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/schema.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceIntegrationTest {

    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private StatsClient statsClient;

    @BeforeEach
    public void setUp() {
        eventService = new EventServiceImpl(eventRepository, userRepository, categoryRepository,
                requestRepository, statsClient);
    }

    @Test
    void createEvent() {
        User user = User.builder()
                .name("Max")
                .email("max@email.com")
                .build();

        Category category = Category.builder()
                .name("category")
                .build();

        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("annotation on this event")
                .category(1L)
                .description("description on this event")
                .eventDate(LocalDateTime.now().plusHours(10))
                .title("title")
                .location(Location.builder().lat(65).lon(78).build())
                .build();

        User userSaved = userRepository.save(user);
        Category categorySaved = categoryRepository.save(category);
        EventFullDto eventFullDto = eventService.createEvent(1L, newEventDto);

        assertThat(eventFullDto.getId().equals(1L));
        assertThat(eventFullDto.getAnnotation().equals("annotation on this event"));
        assertThat(eventFullDto.getTitle().equals("title"));


        assertThat(categorySaved.getId().equals(1L));
        assertThat(categorySaved.getName().equals("category"));


        assertThat(userSaved.getName().equals("Max"));
        assertThat(userSaved.getEmail().equals("max@email.com"));
        assertThat(userSaved.getId().equals(1L));
    }
}
