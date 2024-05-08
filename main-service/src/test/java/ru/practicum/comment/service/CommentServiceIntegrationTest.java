package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.storage.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Sql("/schema.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceIntegrationTest {
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private CommentDto commentDtoToCompare;
    private CommentDto commentDto;
    private UpdateCommentDto updateCommentDto;
    private User user;
    private Event event;
    private Category category;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceImpl(commentRepository, userRepository, eventRepository);

        user = User.builder()
                .name("Max")
                .email("max@email.com")
                .build();

        category = Category.builder()
                .name("category")
                .build();

        event = Event.builder()
                .annotation("annotation on this event")
                .category(category)
                .description("description on this event")
                .eventDate(LocalDateTime.now().plusHours(10))
                .title("title")
                .initiator(user)
                .createdOn(LocalDateTime.now())
                .location(Location.builder().lat(65).lon(78).build())
                .build();

        commentDto = CommentDto.builder()
                .userId(1L)
                .eventId(1L)
                .comment("Comment")
                .build();

        commentDtoToCompare = CommentDto.builder()
                .userId(1L)
                .eventId(1L)
                .comment("Comment")
                .id(1L)
                .edited(false)
                .build();

        updateCommentDto = UpdateCommentDto.builder()
                .comment("Comment Updated")
                .build();
    }

    @Test
    void createComment() {
        userRepository.save(user);
        categoryRepository.save(category);
        eventRepository.save(event);

        CommentDto commentDtoSaved = commentService.createComment(commentDto);
        commentDtoToCompare.setCommentPostingTime(commentDtoSaved.getCommentPostingTime());

        assertThat(commentDtoToCompare.equals(commentDtoSaved));
    }

    @Test
    void getComment() {
        userRepository.save(user);
        categoryRepository.save(category);
        eventRepository.save(event);

        CommentDto commentDtoSaved = commentService.createComment(commentDto);
        commentDtoToCompare.setCommentPostingTime(commentDtoSaved.getCommentPostingTime());

        CommentDto commentToGet = commentService.getComment(1L);
        assertThat(commentDtoToCompare.equals(commentToGet));
    }

    @Test
    void getComments() {
        userRepository.save(user);
        categoryRepository.save(category);
        eventRepository.save(event);

        CommentDto commentDtoSaved = commentService.createComment(commentDto);
        commentDtoToCompare.setCommentPostingTime(commentDtoSaved.getCommentPostingTime());

        Collection<CommentDto> commentsToGet = commentService.getComments(1L);
        assertThat(commentsToGet.equals(List.of(commentDtoToCompare)));
    }

    @Test
    void updateComment() {
        userRepository.save(user);
        categoryRepository.save(category);
        eventRepository.save(event);

        commentService.createComment(commentDto);
        commentDtoToCompare.setCommentPostingTime(updateCommentDto.getCommentPostingTime());
        commentDtoToCompare.setComment(updateCommentDto.getComment());
        commentDtoToCompare.setEdited(updateCommentDto.getEdited());

        CommentDto commentUpdated = commentService.updateComment(updateCommentDto, 1L, 1L);
        assertThat(commentUpdated.equals(commentDtoToCompare));
    }

    @Test
    void deleteComment() {
        userRepository.save(user);
        categoryRepository.save(category);
        eventRepository.save(event);

        commentService.createComment(commentDto);
        commentService.deleteComment(1L, 1L);

        assertThrows(ObjectNotFoundException.class, () -> commentService.deleteComment(1L, 1L));
    }
}