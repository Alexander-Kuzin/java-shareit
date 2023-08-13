package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;

import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utilities.Constants.SORT_BY_START_DATE_DESC;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Transactional(readOnly = true)
    @Override
    public List<GetBookingDto> getUserBookings(long userId, String stateString) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        State state = State.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findByBooker(user, SORT_BY_START_DATE_DESC);
                break;
            case CURRENT:
                bookings = bookingStorage.findByBookerCurrent(user, currentMoment, SORT_BY_START_DATE_DESC);
                break;
            case PAST:
                bookings = bookingStorage.findByBookerPast(user, currentMoment, SORT_BY_START_DATE_DESC);
                break;
            case FUTURE:
                bookings = bookingStorage.findByBookerFuture(user, currentMoment, SORT_BY_START_DATE_DESC);
                break;
            case WAITING:
                bookings = bookingStorage.findByBookerAndStatus(user, Status.WAITING, SORT_BY_START_DATE_DESC);
                break;
            case REJECTED:
                bookings = bookingStorage.findByBookerAndStatus(user, Status.REJECTED, SORT_BY_START_DATE_DESC);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings.stream()
                .map(BookingMapper::toGetBookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetBookingDto> getOwnerBookings(long userId, String stateString) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        State state = State.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findByItemOwner(user, SORT_BY_START_DATE_DESC);
                break;
            case CURRENT:
                bookings = bookingStorage.findByItemOwnerCurrent(user, currentMoment, SORT_BY_START_DATE_DESC);
                break;
            case PAST:
                bookings = bookingStorage.findByItemOwnerPast(user, currentMoment, SORT_BY_START_DATE_DESC);
                break;
            case FUTURE:
                bookings = bookingStorage.findByItemOwnerFuture(user, currentMoment, SORT_BY_START_DATE_DESC);
                break;
            case WAITING:
                bookings = bookingStorage.findByItemOwnerAndStatus(user, Status.WAITING, SORT_BY_START_DATE_DESC);
                break;
            case REJECTED:
                bookings = bookingStorage.findByItemOwnerAndStatus(user, Status.REJECTED, SORT_BY_START_DATE_DESC);
                break;
            default:
                bookings = Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toGetBookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetBookingDto getBookingByUserOwner(long userId, long bookingId) {
        userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Booking bookingId ID %s", bookingId)));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Booking bookingId ID %s", bookingId));
        }
        return BookingMapper.toGetBookingDtoFromBooking(booking);
    }

    @Override
    public GetBookingDto create(long userId, AddBookingDto addBookingDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        if (addBookingDto.getEnd().isBefore(addBookingDto.getStart()) ||
                addBookingDto.getEnd().isEqual(addBookingDto.getStart())) {
            throw new NotValidDateException("End date can't be before or equal start date");
        }

        Item item = itemStorage.findById(addBookingDto.getItemId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item with ID %s", addBookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new ActionNotAvailableException("Item is not available for booking");
        }
        if (item.getOwner().getId() == userId) {
            throw new EntityNotFoundException("You can't book your own item");
        }

        Booking booking = BookingMapper.toBookingFromCreateBookingDto(addBookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toGetBookingDtoFromBooking(bookingStorage.save(booking));
    }

    @Override
    public GetBookingDto approveBooking(long userId, long bookingId, Boolean approved) {
        userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Booking with ID %s", bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Booking with ID %s", booking.getId()));
        }
        Status status;
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ActionNotAvailableException("Booking already confirmed");
            }
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        booking.setStatus(status);
        return BookingMapper.toGetBookingDtoFromBooking(bookingStorage.save(booking));
    }
}