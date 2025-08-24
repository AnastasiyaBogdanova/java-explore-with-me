package ru.yandex.practicum.ewmmainservice.main.exception;

public class ActionConflictException extends RuntimeException {
    public ActionConflictException(String message) {
        super(message);
    }
}