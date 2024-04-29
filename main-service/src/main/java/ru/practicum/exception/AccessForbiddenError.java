package ru.practicum.exception;

public class AccessForbiddenError extends RuntimeException {
    public AccessForbiddenError(String message) {
        super(message);
    }
}
