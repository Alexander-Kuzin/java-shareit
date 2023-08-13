package ru.practicum.shareit.storages;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getAllByUserId(long userId);

    Item getOneById(long itemId);

    Item add(Item item);

    Item update(long userId, Item item);

    void delete(long userId, long itemId);

    List<Item> search(String text);
}