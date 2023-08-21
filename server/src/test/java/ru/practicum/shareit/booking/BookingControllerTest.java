package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.model.Status;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.user.dto.GetUserForGetBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ru.practicum.shareit.utilities.Constants.DATE_TIME_FORMATTER;
import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static GetUserForGetBookingDto booker;
    private static AddBookingDto createBookingDto;
    private static GetBookingDto getBookingDto;
    private static List<GetBookingDto> listWith20Bookings;

    @BeforeAll
    static void beforeAll() {
        LocalDateTime futureOneDay = LocalDateTime.now().plusDays(1).withNano(0);

        LocalDateTime futureTwoDay = LocalDateTime.now().plusDays(2).withNano(0);

        booker = GetUserForGetBookingDto.builder()
                .id(1L)
                .build();

        GetBookingForItemDto item = GetBookingForItemDto.builder()
                .id(1L)
                .name("item")
                .build();

        createBookingDto = AddBookingDto.builder()
                .itemId(1L)
                .start(futureOneDay)
                .end(futureTwoDay)
                .build();

        getBookingDto = GetBookingDto.builder()
                .id(1L)
                .start(futureOneDay)
                .end(futureTwoDay)
                .status(Status.WAITING)
                .booker(booker)
                .item(item)
                .build();

        listWith20Bookings = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            listWith20Bookings.add(getBookingDto.toBuilder().id(i + 2L).build());
        }
    }

    @Test
    @SneakyThrows
    void testGetExceptionWithCreateBookingWithoutHeader() {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(anyLong(), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void testCreateBooking() {
        when(bookingService.create(anyLong(), any(AddBookingDto.class)))
                .thenReturn(getBookingDto);

        String jsonBooking = objectMapper.writeValueAsString(createBookingDto);

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(jsonBooking))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(getBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(getBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).create(booker.getId(), createBookingDto);
    }

    @Test
    @SneakyThrows
    void testGetExceptionWithApproveBookingWithoutHeader() {
        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void testApproveBooking() {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(getBookingDto);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(getBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(getBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void testRejectBooking() {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(getBookingDto);
        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(getBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(getBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void testGetExceptionWithGetBookingByUserOwnerWithoutHeader() {
        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getBookingByUserOwner(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void testGetBookingWithGetBookingByUserOwner() {
        when(bookingService.getBookingByUserOwner(anyLong(), anyLong()))
                .thenReturn(getBookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(getBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(getBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(getBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(getBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(getBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(getBookingDto.getItem().getName()));
        verify(bookingService, times(1)).getBookingByUserOwner(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void testGetExceptionWithGetUserBookingsWithoutHeader() {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }


    @Test
    @SneakyThrows
    void testGetBookingWithGetUserBookings() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);
        mockMvc.perform(get("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(bookingService, times(1)).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void testGetExceptionWithGetOwnerBookingsWithoutHeader() {
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void testGetBookingWithGetOwnerBookings() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(20))
                .andExpect(jsonPath("$.[0].id").value(2L))
                .andExpect(jsonPath("$.[19].id").value(21L));
        verify(bookingService, times(1)).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

}