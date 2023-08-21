package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.model.Status;
import ru.practicum.shareit.user.dto.GetUserForGetBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@WebMvcTest(value = GatewayBookingController.class)
class GatewayBookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private GatewayBookingController gatewayBookingController;
    private static GetUserForGetBookingDto booker;
    private static AddBookingDto createBookingDto;
    private static GetBookingDto getBookingDto;
    private static List<GetBookingDto> listWith20Bookings;

    @SpringBootApplication
    static class TestConfiguration {
    }


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
    void exceptionWithApproveBookingWithWrongApproveTest() {
        mockMvc.perform(patch("/bookings/1?approve=kek")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetUserBookingsWithFromMoreThenMaxInt() {
        mockMvc.perform(get("/bookings?from=2147483648")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }
}