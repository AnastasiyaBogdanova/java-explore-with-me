package ru.yandex.practicum.ewmmainservice.main.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewmmainservice.main.dto.comment.CommentDto;
import ru.yandex.practicum.ewmmainservice.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        log.info("Getting comment by id: {}", commentId);
        return commentService.getCommentById(commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getEventComments(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting comments for event id: {}, from: {}, size: {}", eventId, from, size);
        return commentService.getCommentsByEventId(eventId, from, size);
    }
}