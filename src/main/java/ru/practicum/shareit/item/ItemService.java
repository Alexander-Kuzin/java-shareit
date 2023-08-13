package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    List<GetItemDto> getAllByUserId(long userId);

    GetItemDto getOneById(long userId, long itemId);

    GetItemDto create(long userId, AddOrUpdateItemDto addOrUpdateItemDto);

    GetItemDto update(long userId, long itemId, AddOrUpdateItemDto updateItemDto);

    void delete(long userId, long itemId);

    List<GetItemDto> search(long userId, String text);

    GetCommentDto createComment(long userId, long itemId, AddCommentDto commentDto);
}