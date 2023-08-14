package ru.practicum.shareit.exceptions;

public class ActionNotAvailableException extends RuntimeException {
    public ActionNotAvailableException(String message) {
        super(message);
    }
}
