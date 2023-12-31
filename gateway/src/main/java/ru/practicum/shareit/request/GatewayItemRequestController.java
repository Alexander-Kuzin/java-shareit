package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.request.dto.AddItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class GatewayItemRequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                @RequestBody
                                                @Valid AddItemRequestDto itemRequestDto) {
        return client.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return client.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @RequestParam(defaultValue = "0")
                                                 @Min(0) @Max(Integer.MAX_VALUE) int from,
                                                 @RequestParam(defaultValue = "20")
                                                 @Min(1) @Max(20) int size) {
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @PathVariable long requestId) {
        return client.getRequestById(userId, requestId);
    }
}
