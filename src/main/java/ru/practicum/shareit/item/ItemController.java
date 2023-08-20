package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.markers.Marker.OnCreate;
import ru.practicum.shareit.markers.Marker.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<GetItemDto> getAllByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                           @RequestParam(defaultValue = "0")
                                           @Min(0) @Max(Integer.MAX_VALUE) int from,
                                           @RequestParam(defaultValue = "20")
                                           @Min(1) @Max(20) int size) {
        return itemService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public GetItemDto getByItemId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                  @PathVariable long itemId) {
        return itemService.getOneById(userId, itemId);
    }

    @PostMapping
    public GetItemDto create(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                             @RequestBody
                             @Validated(OnCreate.class) AddOrUpdateItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public GetItemDto update(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                             @PathVariable long itemId,
                             @RequestBody
                             @Validated(OnUpdate.class) AddOrUpdateItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                       @PathVariable long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public List<GetItemDto> search(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                   @RequestParam String text,
                                   @RequestParam(defaultValue = "0")
                                   @Min(0) @Max(Integer.MAX_VALUE) int from,
                                   @RequestParam(defaultValue = "20")
                                   @Min(1) @Max(20) int size) {
        return itemService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public GetCommentDto createComment(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                       @PathVariable long itemId,
                                       @RequestBody
                                       @Valid AddCommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
