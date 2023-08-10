package ru.practicum.shareit.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public GetItemDto getItemDtoFromItem(Item item) {
        return GetItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item getItemFromAddOrUpdateItemDto(AddOrUpdateItemDto addOrUpdateItemDto) {
        return Item.builder()
                .name(addOrUpdateItemDto.getName())
                .description(addOrUpdateItemDto.getDescription())
                .available(addOrUpdateItemDto.getAvailable())
                .build();
    }
}