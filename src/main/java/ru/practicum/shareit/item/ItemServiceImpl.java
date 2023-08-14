package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.MethodArgumentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.CommentMapper;
import ru.practicum.shareit.mappers.ItemMapper;

import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utilities.Constants.SORT_BY_ID_ASC;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;

    @Transactional(readOnly = true)
    @Override
    public List<GetItemDto> getAllByUserId(long userId) {
        List<Item> items = itemStorage.findAllByOwnerIdWithBookings(userId, SORT_BY_ID_ASC);
        if (!items.isEmpty() && items.get(0).getOwner().getId() == userId) {
            return items.stream()
                    .map(ItemMapper::toGetItemWIthBookingDtoFromItem)
                    .collect(Collectors.toList());
        } else {
            return items.stream()
                    .map(ItemMapper::toGetItemDtoFromItem)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public GetItemDto getOneById(long userId, long itemId) {
        userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = itemStorage.findByIdWithOwner(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s not found", itemId)));
        if (item.getOwner().getId() == userId) {
            return ItemMapper.toGetItemWIthBookingDtoFromItem(item);
        }
        return ItemMapper.toGetItemDtoFromItem(item);
    }

    @Override
    public GetItemDto create(long userId, AddOrUpdateItemDto addOrUpdateItemDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = ItemMapper.toGetItemFromCreateUpdateItemDto(addOrUpdateItemDto);
        item.setOwner(user);
        return ItemMapper.toGetItemDtoFromItem(itemStorage.save(item));
    }

    @Override
    public GetItemDto update(long userId, long itemId, AddOrUpdateItemDto updateItemDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = itemStorage.findByIdWithOwner(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s", itemId)));

        if (!item.getOwner().equals(user)) {
            throw new EntityNotFoundException(
                    String.format("User with ID = %s has no items with ID = %s", user.getId(), item.getId()));
        }

        if (updateItemDto.getName() != null && !updateItemDto.getName().isBlank()) {
            item.setName(updateItemDto.getName());
        }

        if (updateItemDto.getDescription() != null && !updateItemDto.getDescription().isBlank()) {
            item.setDescription(updateItemDto.getDescription());
        }

        if (updateItemDto.getAvailable() != null) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return ItemMapper.toGetItemDtoFromItem(itemStorage.save(item));
    }

    @Override
    public void delete(long userId, long itemId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = itemStorage.findByIdWithOwner(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s", itemId)));
        if (!item.getOwner().equals(user)) {
            throw new EntityNotFoundException(
                    String.format("User with ID = %s has no items with ID = %s", user.getId(), item.getId()));
        }
        itemStorage.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetItemDto> search(long userId, String text) {
        userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text, SORT_BY_ID_ASC)
                .stream()
                .map(ItemMapper::toGetItemDtoFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public GetCommentDto createComment(long userId, long itemId, AddCommentDto commentDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));

        Item item = itemStorage.findByIdWithOwner(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s", userId)));

        if (isBookingByUser(user, item)) {
            Comment comment = CommentMapper.toCommentFromCreateCommentDto(commentDto);
            comment.setAuthor(user);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toGetCommentDtoFromComment(commentStorage.save(comment));
        } else {
            throw new MethodArgumentException(String.format(
                    "User ID = %s booking item ID = %s not found", userId, itemId));
        }
    }

    private Boolean isBookingByUser(User user, Item item) {
        LocalDateTime currentTime = LocalDateTime.now();
        return item.getBookings()
                .stream()
                .anyMatch(t -> t.getBooker().equals(user)
                        && t.getEndDate().isBefore(currentTime));
    }
}