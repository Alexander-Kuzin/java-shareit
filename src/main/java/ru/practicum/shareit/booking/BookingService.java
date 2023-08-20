package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import java.util.List;

public interface BookingService {

    List<GetBookingDto> getUserBookings(long userId, String state, int from, int size);

    List<GetBookingDto> getOwnerBookings(long userId, String state, int from, int size);

    GetBookingDto getBookingByUserOwner(long userId, long bookingId);

    GetBookingDto create(long userid, AddBookingDto addBookingDto);

    GetBookingDto approveBooking(long userId, long bookingId, Boolean approved);
}