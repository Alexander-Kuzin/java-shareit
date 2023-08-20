package ru.practicum.shareit.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.dto.GetItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@UtilityClass
public class BookingMapper {
    public static Booking toBookingFromCreateBookingDto(AddBookingDto addBookingDto) {
        return Booking.builder()
                .startDate(addBookingDto.getStart())
                .endDate(addBookingDto.getEnd())
                .build();
    }

    public static GetItemBookingDto toGetBookingForItemDtoFromBooking(Booking booking) {
        if (booking == null) {
            return null;
        }
        return GetItemBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .build();
    }

    public static GetBookingDto toGetBookingDtoFromBooking(Booking booking) {
        return GetBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .booker(UserMapper.toGetBookingUserDtoFromUser(booking.getBooker()))
                .item(ItemMapper.toGetBookingDtoFromItem(booking.getItem()))
                .build();
    }
}