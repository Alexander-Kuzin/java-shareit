package ru.practicum.shareit.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utilities.Constants.*;

@UtilityClass
public class ItemMapper {
    public static GetItemDto toGetItemDtoFromItem(Item item) {
        SortedSet<GetCommentDto> comments = new TreeSet<>(orderByCreatedDesc);

        if (item.getComments() != null) {
            comments.addAll(item.getComments()
                    .stream()
                    .map(CommentMapper::toGetCommentDtoFromComment)
                    .collect(Collectors.toSet()));
        }

        return GetItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }

    public static GetItemDto toGetItemWIthBookingDtoFromItem(Item item) {
        LocalDateTime currentTime = LocalDateTime.now();

        GetItemDto getItemDto = toGetItemDtoFromItem(item);

        Set<Booking> bookings = item.getBookings();

        Booking lastBooking = bookings
                .stream()
                .sorted(orderByStartDateDesc)
                .filter(t -> t.getStartDate().isBefore(currentTime) &&
                        t.getStatus().equals(Status.APPROVED))
                .findFirst()
                .orElse(null);

        Booking nextBooking = bookings
                .stream()
                .sorted(orderByStartDateAsc)
                .filter(t -> t.getStartDate().isAfter(currentTime) &&
                        t.getStatus().equals(Status.APPROVED))
                .findFirst()
                .orElse(null);

        getItemDto.setLastBooking(BookingMapper.toGetItemBookingDtoFromBooking(lastBooking));
        getItemDto.setNextBooking(BookingMapper.toGetItemBookingDtoFromBooking(nextBooking));

        return getItemDto;
    }

    public static Item toGetItemFromCreateUpdateItemDto(AddOrUpdateItemDto addOrUpdateItemDto) {
        return Item.builder()
                .name(addOrUpdateItemDto.getName())
                .description(addOrUpdateItemDto.getDescription())
                .available(addOrUpdateItemDto.getAvailable())
                .build();
    }

    public static GetBookingForItemDto toGetBookingDtoFromItem(Item item) {
        return GetBookingForItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}