package ru.practicum.shareit.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequestFromAddItemRequestDto(AddItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public GetItemRequestDto toGetItemRequestDtoFromItemRequest(ItemRequest itemRequest) {
        return GetItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ? itemRequest.getItems()
                        .stream()
                        .map(ItemMapper::toGetItemForGetItemRequestDtoFromItem)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}