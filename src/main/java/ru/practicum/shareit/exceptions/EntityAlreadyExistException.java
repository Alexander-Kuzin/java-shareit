package ru.practicum.shareit.exceptions;

public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(final String message) {
        super(message);
    }
}
