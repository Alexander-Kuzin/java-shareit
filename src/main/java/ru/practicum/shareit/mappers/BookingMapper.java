package ru.practicum.shareit.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.*;

@UtilityClass
public class BookingMapper {
    public static Booking toBookingFromCreateBookingDto(AddBookingDto addBookingDto) {
        return Booking.builder()
                .startDate(addBookingDto.getStart())
                .endDate(addBookingDto.getEnd())
                .build();
    }

    public static GetItemBookingDto toGetItemBookingDtoFromBooking(Booking booking) {
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