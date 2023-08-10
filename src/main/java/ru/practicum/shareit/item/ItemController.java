package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.services.ItemService;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.markers.Marker;


import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<GetItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public GetItemDto getByItemId(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long itemId) {
        return itemService.getOneById(userId, itemId);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public GetItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody
                          @Valid AddOrUpdateItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public GetItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long itemId,
                             @RequestBody
                             @Validated(Marker.OnUpdate.class) AddOrUpdateItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public List<GetItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam String text) {
        return itemService.search(userId, text);
    }
}