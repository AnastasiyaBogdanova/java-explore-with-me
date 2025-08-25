package ru.yandex.practicum.ewmmainservice.main.mapper;

import ru.yandex.practicum.ewmmainservice.main.dto.comment.CommentDto;
import ru.yandex.practicum.ewmmainservice.main.model.Comment;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent() != null ? comment.getEvent().getId() : null)
                .authorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null)
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                .created(comment.getCreated())
                .lastModified(comment.getLastModified())
                .build();
    }
}