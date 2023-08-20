package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void testHandleNotFound() {
        EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Request entity data was not found");
        ErrorResponse result = errorHandler.handleEntityNotFoundException(entityNotFoundException);
        assertEquals("Request entity data was not found", result.getError());
    }

    @Test
    void testHandleConflict() {
        EntityAlreadyExistException alreadyExistsException = new EntityAlreadyExistException("Entity error");
        ErrorResponse result = errorHandler.handleEntityAlreadyExistException(alreadyExistsException);
        assertEquals("Entity error", result.getError());
    }
}