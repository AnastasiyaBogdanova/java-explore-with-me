package ru.yandex.practicum.ewmmainservice.main.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewmmainservice.main.dto.comment.CommentDto;
import ru.yandex.practicum.ewmmainservice.main.dto.comment.NewCommentDto;
import ru.yandex.practicum.ewmmainservice.main.dto.comment.UpdateCommentRequest;
import ru.yandex.practicum.ewmmainservice.main.exception.ActionConflictException;
import ru.yandex.practicum.ewmmainservice.main.exception.NotFoundException;
import ru.yandex.practicum.ewmmainservice.main.mapper.CommentMapper;
import ru.yandex.practicum.ewmmainservice.main.model.Comment;
import ru.yandex.practicum.ewmmainservice.main.model.Event;
import ru.yandex.practicum.ewmmainservice.main.model.User;
import ru.yandex.practicum.ewmmainservice.main.repository.CommentRepository;
import ru.yandex.practicum.ewmmainservice.main.repository.EventRepository;
import ru.yandex.practicum.ewmmainservice.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Comment not found with id: " + commentId + " for user: " + userId);
        }

        // Проверяем, что прошло не более 24 часов с момента создания
        if (comment.getCreated().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new ActionConflictException("Cannot update comment after 24 hours from creation");
        }

        comment.setText(updateRequest.getText());
        comment.setLastModified(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        return CommentMapper.toDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        if (!commentRepository.existsByIdAndAuthorId(commentId, userId)) {
            throw new NotFoundException("Comment not found with id: " + commentId + " for user: " + userId);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));
        return CommentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByEventId(eventId, pageable);
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByAuthorId(userId, pageable);
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEventIdAndUserId(Long eventId, Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByEventIdAndAuthorId(eventId, userId, pageable);
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}