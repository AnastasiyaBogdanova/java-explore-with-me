package ru.yandex.practicum.ewmmainservice.main.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
