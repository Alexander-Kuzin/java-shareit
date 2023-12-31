package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;

import ru.practicum.shareit.markers.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collections;

import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class GatewayItemController {
    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @RequestParam(defaultValue = "0")
                                                 @Min(0) @Max(Integer.MAX_VALUE) int from,
                                                 @RequestParam(defaultValue = "20")
                                                 @Min(1) @Max(20) int size) {
        return client.getAllByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByItemId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                              @PathVariable long itemId) {
        return client.getOneById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                         @RequestBody
                                         @Validated(Marker.OnCreate.class) AddOrUpdateItemDto itemDto) {
        return client.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                         @PathVariable long itemId,
                                         @RequestBody
                                         @Validated(Marker.OnUpdate.class) AddOrUpdateItemDto itemDto) {
        return client.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                       @PathVariable long itemId) {
        client.delete(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0")
                                         @Min(0) @Max(Integer.MAX_VALUE) int from,
                                         @RequestParam(defaultValue = "20")
                                         @Min(1) @Max(20) int size) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return client.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                @PathVariable long itemId,
                                                @RequestBody
                                                @Valid AddCommentDto commentDto) {
        return client.createComment(userId, itemId, commentDto);
    }
}