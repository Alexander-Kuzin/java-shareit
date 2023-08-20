package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.ActionNotAvailableException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.NotValidDateException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.GetUserForGetBookingDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    private static BookingService bookingService;
    private static BookingStorage bookingStorage;
    private static ItemStorage itemStorage;
    private static UserStorage userStorage;
    private static User user;
    private static User user2;
    private static Item item;
    private static AddBookingDto bookingDto;
    private static LocalDateTime startTime;
    private static LocalDateTime endTime;
    private static GetUserForGetBookingDto booker;
    private static Booking booking;
    private static GetBookingForItemDto itemDto;
    private static List<Booking> listOfBookings;

    @BeforeAll
    static void beforeAll() {
        startTime = LocalDateTime.now().minusDays(2);

        endTime = LocalDateTime.now().minusDays(1);

        user = User.builder()
                .id(1L)
                .name("userName")
                .email("mail@ya.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("userName2")
                .email("mail2@ya.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        bookingDto = AddBookingDto.builder()
                .itemId(1L)
                .start(startTime)
                .end(endTime)
                .build();

        booker = GetUserForGetBookingDto.builder()
                .id(2L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startDate(startTime)
                .endDate(endTime)
                .booker(user2)
                .status(Status.WAITING)
                .item(item)
                .build();

        itemDto = GetBookingForItemDto.builder()
                .id(1L)
                .name("itemName")
                .build();

        listOfBookings = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfBookings.add(booking.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        bookingStorage = Mockito.mock(BookingStorage.class);
        itemStorage = Mockito.mock(ItemStorage.class);
        userStorage = Mockito.mock(UserStorage.class);
        bookingService = new BookingServiceImpl(bookingStorage, userStorage, itemStorage);
    }

    @Test
    void createBookingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        GetBookingDto getBookingDto = bookingService.create(2L, bookingDto);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.WAITING)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    void getExceptionCreateBookingNotFoundUserTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.create(2L, bookingDto));

        assertEquals("User with ID 2",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void getExceptionCreateBookingNotFoundItemTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.create(2L, bookingDto));

        assertEquals("Item with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void getExceptionCreateBookingNotValidDateExceptionTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final NotValidDateException exception = Assertions.assertThrows(
                NotValidDateException.class,
                () -> bookingService.create(2L, bookingDto.toBuilder().start(endTime).end(startTime).build()));

        assertEquals("End date can't be before or equal start date",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, never()).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void getExceptionCreateBookingNotAvailableExceptionTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item.toBuilder().available(false).build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final ActionNotAvailableException exception = Assertions.assertThrows(
                ActionNotAvailableException.class,
                () -> bookingService.create(2L, bookingDto));

        assertEquals("Item is not available for booking",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void getExceptionCreateBookingNotFoundSelfItemTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.create(1L, bookingDto));

        assertEquals("You can't book your own item",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void approveBookingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking.toBuilder().status(Status.APPROVED).build());

        GetBookingDto getBookingDto = bookingService.approveBooking(1L, 1L, true);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.APPROVED)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void rejectBookingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking.toBuilder().status(Status.REJECTED).build());

        GetBookingDto getBookingDto = bookingService.approveBooking(1L, 1L, false);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.REJECTED)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getExceptionWithApproveBookingNoFoundUserTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.approveBooking(1L, 1L, true));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, never()).findById(anyLong());
    }

    @Test
    void getExceptionWithApproveBookingNoFoundBookingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.approveBooking(1L, 1L, true));

        assertEquals("Booking with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getExceptionWithApproveBookingNoFoundOwnerTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.approveBooking(2L, 1L, true));

        assertEquals("Booking with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getExceptionWithApproveBookingNotAvailableAlreadyApprovedTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().status(Status.APPROVED).build()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(booking);

        final ActionNotAvailableException exception = Assertions.assertThrows(
                ActionNotAvailableException.class,
                () -> bookingService.approveBooking(1L, 1L, true));

        assertEquals("Booking already confirmed",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).save(any(Booking.class));
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getBookingByUserOwnerItemTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        GetBookingDto getBookingDto = bookingService.getBookingByUserOwner(1L, 1L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.WAITING)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getBookingByUserOwnerBookingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        GetBookingDto getBookingDto = bookingService.getBookingByUserOwner(2L, 1L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", startTime)
                .hasFieldOrPropertyWithValue("end", endTime)
                .hasFieldOrPropertyWithValue("status", Status.WAITING)
                .hasFieldOrPropertyWithValue("item", itemDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getExceptionWithGetBookingByUserOwnerNotFoundUserTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getBookingByUserOwner(1L, 1L));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findById(anyLong());
    }

    @Test
    void getExceptionWithGetBookingByUserOwnerNotFoundBookingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getBookingByUserOwner(1L, 1L));

        assertEquals("Booking bookingId ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getExceptionWithGetBookingByUserOwnerNotFoundOwnerTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking.toBuilder().build()));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getBookingByUserOwner(3L, 1L));

        assertEquals("Booking bookingId ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getUserBookingsWithAllTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "aLl", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void getExceptionWithGetUserBookingsWithAllTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getUserBookings(1L, "aLl", 1, 5));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findAllByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void getUserBookingsWithCurrentTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "cuRRenT", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByBookerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getUserBookingsWithPastTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "pAST", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByBookerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getUserBookingsWithFutureTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "FUTURE", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByBookerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getUserBookingsWithWaitingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerAndStatus(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "WAITING", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByBookerAndStatus(any(User.class), any(Status.class), any(Pageable.class));
    }

    @Test
    void getUserBookingsWithRejectTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerAndStatus(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "rejected", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByBookerAndStatus(any(User.class), any(Status.class), any(Pageable.class));
    }

    @Test
    void getOwnerBookingsWithAllTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByItemOwner(any(User.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "aLl", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByItemOwner(any(User.class), any(Pageable.class));
    }

    @Test
    void getExceptionWithGetOwnerBookingsWithAllTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingStorage.findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getOwnerBookings(1L, "aLl", 1, 5));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, never()).findAllByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void getOwnerBookingsWithCurrentTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByItemOwnerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "cuRRenT", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByItemOwnerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getOwnerBookingsWithPastTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByItemOwnerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "pAST", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByItemOwnerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getOwnerBookingsWithFutureTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByItemOwnerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "FUTURE", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByItemOwnerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getOwnerBookingsWithWaitingTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByItemOwnerAndStatus(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "WAITING", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByItemOwnerAndStatus(any(User.class), any(Status.class), any(Pageable.class));
    }

    @Test
    void getOwnerBookingsWithRejectTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByItemOwnerAndStatus(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(listOfBookings);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "rejected", 1, 5);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L));
        verify(userStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).findAllByItemOwnerAndStatus(any(User.class), any(Status.class), any(Pageable.class));
    }
}