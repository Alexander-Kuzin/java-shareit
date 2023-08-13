package ru.practicum.shareit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.storages.ItemStorage;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.mappers.ItemMapper;
import ru.practicum.shareit.storages.UserStorage;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<GetItemDto> getAllByUserId(long userId) {
        userStorage.getById(userId);
        return itemStorage.getAllByUserId(userId)
                .stream()
                .map(ItemMapper::getItemDtoFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public GetItemDto getOneById(long userId, long itemId) {
        userStorage.getById(userId);
        return ItemMapper.getItemDtoFromItem(itemStorage.getOneById(itemId));
    }

    @Override
    public GetItemDto add(long userId, AddOrUpdateItemDto addOrUpdateItemDto) {
        User user = userStorage.getById(userId);
        Item item = ItemMapper.getItemFromAddOrUpdateItemDto(addOrUpdateItemDto);
        item.setOwner(user);
        return ItemMapper.getItemDtoFromItem(itemStorage.add(item));
    }

    @Override
    public GetItemDto update(long userId, long itemId, AddOrUpdateItemDto updateItemDto) {
        User user = userStorage.getById(userId);
        Item item = itemStorage.getOneById(itemId);
        item.setOwner(user);
        if (updateItemDto.getName() != null && !updateItemDto.getName().isBlank()) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.getDescription() != null && !updateItemDto.getDescription().isBlank()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.getAvailable() != null) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return ItemMapper.getItemDtoFromItem(itemStorage.update(userId, item));
    }

    @Override
    public void delete(long userId, long itemId) {
        userStorage.getById(userId);
        itemStorage.delete(userId, itemId);
    }

    @Override
    public List<GetItemDto> search(long userId, String text) {
        userStorage.getById(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text)
                .stream()
                .map(ItemMapper::getItemDtoFromItem)
                .collect(Collectors.toList());
    }
}