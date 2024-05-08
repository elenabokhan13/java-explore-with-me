package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.storage.CommentRepository;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.user.storage.UserRepository;
import ru.practicum.validator.CommentValidator;
import ru.practicum.validator.EventValidator;
import ru.practicum.validator.UserValidator;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final EventRepository eventRepository;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        UserValidator.validateUserExists(userRepository, commentDto.getUserId());
        EventValidator.validateEventExists(eventRepository, commentDto.getEventId());
        return CommentMapper.commentToDto(commentRepository.save(CommentMapper.commentFromDto(commentDto)));
    }

    @Override
    public CommentDto getComment(Long commentId) {
        return CommentMapper.commentToDto(CommentValidator.validateCommentExists(commentRepository, commentId));
    }

    @Override
    public Collection<CommentDto> getComments(Long eventId) {
        EventValidator.validateEventExists(eventRepository, eventId);

        return commentRepository.findByEventId(eventId).stream().map(CommentMapper::commentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long commentId) {
        UserValidator.validateUserExists(userRepository, userId);
        Comment comment = CommentValidator.validateCommentExists(commentRepository, commentId);
        CommentValidator.validateUserHasAccessToComment(userId, comment);

        comment.setEdited(updateCommentDto.getEdited());
        comment.setComment(updateCommentDto.getComment());
        comment.setCommentPostingTime(updateCommentDto.getCommentPostingTime());
        return CommentMapper.commentToDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        UserValidator.validateUserExists(userRepository, userId);
        Comment comment = CommentValidator.validateCommentExists(commentRepository, commentId);
        CommentValidator.validateUserHasAccessToComment(userId, comment);

        commentRepository.deleteById(commentId);
    }
}
