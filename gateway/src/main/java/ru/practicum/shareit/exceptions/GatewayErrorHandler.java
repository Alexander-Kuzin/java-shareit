package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GatewayErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GatewayErrorResponse handleEntityNotFoundException(final RuntimeException e) {
        log.error("Response status 404 Not found {}", e.getMessage(), e);
        return new GatewayErrorResponse("Request entity data was not found", e.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public GatewayErrorResponse handleEntityAlreadyExistException(final EntityAlreadyExistException e) {
        log.error("Response status 409 Entity conflict {}", e.getMessage(), e);
        return new GatewayErrorResponse("Entity error", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Response status 400 Bad request {}", e.getMessage(), e);
        return Map.of("error", e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessageTemplate)
                .findFirst().orElse("No message"));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            MethodArgumentException.class,
            ServletRequestBindingException.class,
            ValidationException.class,
            ActionNotAvailableException.class,
            NotValidDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleThrowable(final RuntimeException e) {
        log.error("Response status 400 Bad request {}", e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }
}
