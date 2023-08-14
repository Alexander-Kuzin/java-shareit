package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.utilities.ChunkRequest;

import java.time.LocalDateTime;
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
    public List<GetBookingDto> getUserBookings(long userId, String stateString, int from, int size) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        State state = State.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        Pageable pageable = new ChunkRequest(from, size, SORT_BY_START_DATE_DESC);

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBooker(user, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByBookerAndCurrent(user, currentMoment, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerAndPast(user, currentMoment, pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerAndFuture(user, currentMoment, pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerAndStatus(user, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerAndStatus(user, Status.REJECTED, pageable);
                break;
            default:
                throw new MethodArgumentException(String.format("Illegal state = %s", state));
        }

        return bookings.stream()
                .map(BookingMapper::toGetBookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetBookingDto> getOwnerBookings(long userId, String stateString, int from, int size) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", userId)));
        State state = State.valueOf(stateString.toUpperCase());
        LocalDateTime currentMoment = LocalDateTime.now();
        List<Booking> bookings;

        Pageable pageable = new ChunkRequest(from, size, SORT_BY_START_DATE_DESC);

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItemOwner(user, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByItemOwnerAndCurrent(user, currentMoment, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findAllByItemOwnerAndPast(user, currentMoment, pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItemOwnerAndFuture(user, currentMoment, pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByItemOwnerAndStatus(user, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByItemOwnerAndStatus(user, Status.REJECTED, pageable);
                break;
            default:
                throw new MethodArgumentException(String.format("Illegal state = %s", state));
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
            if (booking.getStatus() == Status.APPROVED) {
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