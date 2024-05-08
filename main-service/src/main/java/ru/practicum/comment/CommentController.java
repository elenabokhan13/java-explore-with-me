package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/comment")
public class CommentController {
    @Autowired
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto commentDto) {
        log.info("Получен запрос к эндпойнту /comment для создания комментария от пользователя по id={} к событию по id={}",
                commentDto.getUserId(), commentDto.getEventId());
        return new ResponseEntity<>(commentService.createComment(commentDto), HttpStatus.CREATED);
    }

    @GetMapping
    public CommentDto getComment(@RequestParam Long commentId) {
        log.info("Получен запрос к эндпойнту /comment для получения комментария по id={}", commentId);
        return commentService.getComment(commentId);
    }

    @GetMapping(value = "/all")
    public Collection<CommentDto> getComments(@RequestParam Long eventId) {
        log.info("Получен запрос к эндпойнту /comment/all для получения всех комментариев к событию по id={}", eventId);
        return commentService.getComments(eventId);
    }

    @PatchMapping
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDto updateCommentDto,
                                    @RequestParam Long userId, @RequestParam Long commentId) {
        log.info("Получен запрос к эндпойнту /comment для обновления комментария по id={} от пользователя по id={}",
                commentId, userId);
        return commentService.updateComment(updateCommentDto, userId, commentId);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteComment(@RequestParam Long commentId, @RequestParam Long userId) {
        log.info("Получен запрос к эндпойнту /comment для удаления комментария по id={} от пользователя по id={}",
                commentId, userId);
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
