package ru.yandex.practicum.ewmmainservice.main.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewmmainservice.main.dto.comment.CommentDto;
import ru.yandex.practicum.ewmmainservice.main.dto.comment.NewCommentDto;
import ru.yandex.practicum.ewmmainservice.main.dto.comment.UpdateCommentRequest;
import ru.yandex.practicum.ewmmainservice.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable Long userId,
            @RequestParam Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Creating comment for user id: {}, event id: {}", userId, eventId);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest updateRequest) {
        log.info("Updating comment id: {} for user id: {}", commentId, userId);
        return commentService.updateComment(userId, commentId, updateRequest);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId) {
        log.info("Deleting comment id: {} for user id: {}", commentId, userId);
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting comments for user id: {}, from: {}, size: {}", userId, from, size);
        return commentService.getCommentsByUserId(userId, from, size);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getUserCommentsForEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting comments for user id: {} and event id: {}, from: {}, size: {}", userId, eventId, from, size);
        return commentService.getCommentsByEventIdAndUserId(eventId, userId, from, size);
    }
}