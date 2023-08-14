package ru.practicum.shareit.exceptions;

public class MethodArgumentException extends RuntimeException {
    public MethodArgumentException(String message) {
        super(message);
    }
}