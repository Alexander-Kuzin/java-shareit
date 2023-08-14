package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.AddBookingDto;

import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.AddOrUpdateItemDto;

import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingStorageTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    private static AddOrUpdateUserDto userDto;
    private static AddOrUpdateUserDto userDto2;
    private static AddOrUpdateItemDto itemDto;
    private static AddBookingDto bookingDto;

    @BeforeAll
    static void beforeAll() {
        userDto = AddOrUpdateUserDto.builder()
                .name("userName")
                .email("email@ya.ru")
                .build();

        userDto2 = AddOrUpdateUserDto.builder()
                .name("userName2")
                .email("email2@ya.ru")
                .build();

        itemDto = AddOrUpdateItemDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        bookingDto = AddBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void CreateBookingTest() {
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(1L, itemDto);
        bookingService.create(2L, bookingDto);

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertThat(1L, equalTo(booking.getItem().getId()));
        assertThat(2L, equalTo(booking.getBooker().getId()));
        assertThat(Status.WAITING, equalTo(booking.getStatus()));
    }

    @Test
    void ApproveBookingTest() {
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(1L, itemDto);
        bookingService.create(2L, bookingDto);
        bookingService.approveBooking(1L, 1L, true);

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertThat(1L, equalTo(booking.getItem().getId()));
        assertThat(2L, equalTo(booking.getBooker().getId()));
        assertThat(Status.APPROVED, equalTo(booking.getStatus()));
    }

    @Test
    void GetBookingByUserOwnerTest() {
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(1L, itemDto);
        bookingService.create(2L, bookingDto);
        bookingService.approveBooking(1L, 1L, true);

        GetBookingDto getBookingDto = bookingService.getBookingByUserOwner(1L, 1L);

        assertThat(1L, equalTo(getBookingDto.getItem().getId()));
        assertThat(2L, equalTo(getBookingDto.getBooker().getId()));
        assertThat(Status.APPROVED, equalTo(getBookingDto.getStatus()));
    }

    @Test
    void GetUserBookingsTest() {
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(1L, itemDto);
        itemService.create(2L, itemDto);
        bookingService.create(2L, bookingDto);
        bookingService.create(2L, bookingDto);
        bookingService.create(2L, bookingDto);
        bookingService.create(2L, bookingDto);
        bookingService.create(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(1)).build());
        bookingService.create(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(2)).build());
        bookingService.approveBooking(1L, 1L, true);

        List<GetBookingDto> bookings = bookingService.getUserBookings(1L, "all",1, 3);

        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 5L));
    }

    @Test
    void GetOwnerBookingsTest() {
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(1L, itemDto);
        itemService.create(2L, itemDto);
        bookingService.create(2L, bookingDto);
        bookingService.create(2L, bookingDto.toBuilder().start(LocalDateTime.now().minusDays(2).plusHours(3)).build());
        bookingService.create(2L, bookingDto.toBuilder().start(LocalDateTime.now().minusDays(2).plusHours(2)).build());
        bookingService.create(2L, bookingDto.toBuilder().start(LocalDateTime.now().minusDays(2).plusHours(1)).build());
        bookingService.create(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(2)).build());
        bookingService.create(1L, bookingDto.toBuilder().itemId(2L).start(LocalDateTime.now().minusDays(2).plusHours(1)).build());
        bookingService.approveBooking(1L, 1L, true);
        bookingService.approveBooking(2L, 5L, true);

        List<GetBookingDto> bookings = bookingService.getOwnerBookings(1L, "all",1, 3);

        Assertions.assertThat(bookings)
                .isNotEmpty()
                .hasSize(3)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 3L);
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 4L);
                    Assertions.assertThat(list.get(2)).hasFieldOrPropertyWithValue("id", 1L);
                });
    }
}