package ru.practicum.validator;

import ru.practicum.comment.model.Comment;
import ru.practicum.comment.storage.CommentRepository;
import ru.practicum.exception.AccessForbiddenError;
import ru.practicum.exception.ObjectNotFoundException;

import java.util.Objects;

public class CommentValidator {
    public static Comment validateCommentExists(CommentRepository commentRepository, Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Comment with id=" + id + " was not found"));
    }

    public static void validateUserHasAccessToComment(Long userId, Comment comment) {
        if (!Objects.equals(userId, comment.getUserId())) {
            throw new AccessForbiddenError("User with id=" + userId + " doesn't have access to edit comment with id="
                    + comment.getUserId());
        }
    }
}
