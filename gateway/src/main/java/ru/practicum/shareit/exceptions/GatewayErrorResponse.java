package ru.practicum.shareit.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GatewayErrorResponse {
    private final String error;
    private final String description;
}