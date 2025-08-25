package ru.yandex.practicum.ewmmainservice.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewmmainservice.main.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByEventId(Long eventId, Pageable pageable);

    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId AND c.author.id = :authorId")
    Page<Comment> findByEventIdAndAuthorId(@Param("eventId") Long eventId,
                                           @Param("authorId") Long authorId,
                                           Pageable pageable);

    boolean existsByIdAndAuthorId(Long commentId, Long authorId);

}