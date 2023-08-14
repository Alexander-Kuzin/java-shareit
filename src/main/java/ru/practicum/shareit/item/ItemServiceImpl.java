package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.MethodArgumentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.CommentMapper;
import ru.practicum.shareit.mappers.ItemMapper;

import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utilities.ChunkRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utilities.Constants.SORT_BY_ID_ASC;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestStorage requestStorage;

    @Transactional(readOnly = true)
    @Override
    public List<GetItemDto> getAllByUserId(long userId, int from, int size) {
        Pageable pageable = new ChunkRequest(from, size, SORT_BY_ID_ASC);
        List<Item> items = itemStorage.findAllByOwnerId(userId, pageable).getContent();
        if (!items.isEmpty() && items.get(0).getOwner().getId() == userId) {
            return items.stream()
                    .map(ItemMapper::toGetItemWIthBookingDtoFromItem)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public GetItemDto getOneById(long userId, long itemId) {
        userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s not found", itemId)));
        if (item.getOwner().getId() == userId) {
            return ItemMapper.toGetItemWIthBookingDtoFromItem(item);
        } else {
            return ItemMapper.toGetItemDtoFromItem(item);
        }
    }

    @Override
    public GetItemDto create(long userId, AddOrUpdateItemDto createUpdateItemDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = ItemMapper.toItemFromAddOrUpdateItemDto(createUpdateItemDto);
        item.setOwner(user);
        if (createUpdateItemDto.getRequestId() != null) {
            ItemRequest request = requestStorage.findById(createUpdateItemDto.getRequestId()).orElseThrow(
                    () -> new EntityNotFoundException(
                            String.format("Request with ID %s", createUpdateItemDto.getRequestId())));
            item.setRequest(request);
        }
        return ItemMapper.toGetItemDtoFromItem(itemStorage.save(item));
    }

    @Override
    public GetItemDto update(long userId, long itemId, AddOrUpdateItemDto updateItemDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s", itemId)));
        if (!item.getOwner().equals(user)) {
            throw new EntityNotFoundException(
                    String.format("User with ID = %s has no items with ID = %s", user.getId(), item.getId()));
        }
        if (StringUtils.isNotBlank(updateItemDto.getName())) {
            item.setName(updateItemDto.getName());
        }

        if (StringUtils.isNotBlank(updateItemDto.getDescription())) {
            item.setDescription(updateItemDto.getDescription());
        }
        Optional.ofNullable(updateItemDto.getAvailable()).ifPresent(
                var -> item.setAvailable(updateItemDto.getAvailable())); //fixme
        return ItemMapper.toGetItemDtoFromItem(itemStorage.save(item));
    }

    @Override
    public void delete(long userId, long itemId) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s", itemId)));
        if (!item.getOwner().equals(user)) {
            throw new EntityNotFoundException(
                    String.format("User with ID = %s has no items with ID = %s", user.getId(), item.getId()));
        }
        itemStorage.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetItemDto> search(long userId, String text, int from, int size) {
        userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = new ChunkRequest(from, size, SORT_BY_ID_ASC);
        return itemStorage.search(text, pageable)
                .getContent()
                .stream()
                .map(ItemMapper::toGetItemDtoFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public GetCommentDto createComment(long userId, long itemId, AddCommentDto commentDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s", itemId)));

        if (isBookingByUser(user, item)) {
            Comment comment = CommentMapper.toCommentFromCreateCommentDto(commentDto);
            comment.setAuthor(user);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toGetCommentDtoFromComment(commentStorage.save(comment));
        }
        throw new MethodArgumentException(String.format("User ID = %s did not book item ID = %s", userId, itemId));
    }

    private Boolean isBookingByUser(User user, Item item) {
        LocalDateTime currentTime = LocalDateTime.now();
        return item.getBookings() != null && item.getBookings().stream()
                .anyMatch(t -> t.getBooker().equals(user)
                        && t.getEndDate().isBefore(currentTime)
                        && t.getStatus() == Status.APPROVED);
    }
}