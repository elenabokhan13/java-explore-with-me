package ru.practicum.comment.dto;

import ru.practicum.comment.model.Comment;

public class CommentMapper {
    public static Comment commentFromDto(CommentDto commentDto) {
        return Comment.builder()
                .userId(commentDto.getUserId())
                .eventId(commentDto.getEventId())
                .comment(commentDto.getComment())
                .commentPostingTime(commentDto.getCommentPostingTime())
                .edited(commentDto.getEdited())
                .build();
    }

    public static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .eventId(comment.getEventId())
                .comment(comment.getComment())
                .commentPostingTime(comment.getCommentPostingTime())
                .edited(comment.getEdited())
                .build();
    }
}
