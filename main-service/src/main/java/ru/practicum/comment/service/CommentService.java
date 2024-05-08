package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

import java.util.Collection;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);

    CommentDto getComment(Long commentId);

    Collection<CommentDto> getComments(Long eventId);

    CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long commentId);

    void deleteComment(Long commentId, Long userId);
}
