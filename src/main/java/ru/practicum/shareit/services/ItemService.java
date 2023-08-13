package ru.practicum.shareit.services;

import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;

import java.util.List;

public interface ItemService {
    List<GetItemDto> getAllByUserId(long userId);

    GetItemDto getOneById(long userId, long itemId);

    GetItemDto add(long userId, AddOrUpdateItemDto addOrUpdateItemDto);

    GetItemDto update(long userId, long itemId, AddOrUpdateItemDto updateItemDto);

    void delete(long userId, long itemId);

    List<GetItemDto> search(long userId, String text);
}