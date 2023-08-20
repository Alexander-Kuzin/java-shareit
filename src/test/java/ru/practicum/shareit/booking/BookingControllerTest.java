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
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.user.dto.GetBookingUserDto;

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

    private static GetBookingUserDto booker;
    private static GetBookingForItemDto item;
    private static AddBookingDto createBookingDto;
    private static AddBookingDto createBookingDtoWithStartInPast;
    private static AddBookingDto createBookingDtoWithEndInPast;
    private static AddBookingDto createBookingDtoWithoutStart;
    private static AddBookingDto createBookingDtoWithoutEnd;
    private static AddBookingDto createBookingDtoWithoutItemId;
    private static LocalDateTime pastOneDay;
    private static LocalDateTime futureOneDay;
    private static LocalDateTime futureTwoDay;
    private static GetBookingDto getBookingDto;
    private static List<GetBookingDto> listWith20Bookings;

    @BeforeAll
    static void beforeAll() {
        pastOneDay = LocalDateTime.now().minusDays(1).withNano(0);

        futureOneDay = LocalDateTime.now().plusDays(1).withNano(0);

        futureTwoDay = LocalDateTime.now().plusDays(2).withNano(0);

        booker = GetBookingUserDto.builder()
                .id(1L)
                .build();

        item = GetBookingForItemDto.builder()
                .id(1L)
                .name("item")
                .build();

        createBookingDto = AddBookingDto.builder()
                .itemId(1L)
                .start(futureOneDay)
                .end(futureTwoDay)
                .build();

        createBookingDtoWithStartInPast = AddBookingDto.builder()
                .itemId(1L)
                .start(pastOneDay)
                .end(futureTwoDay)
                .build();

        createBookingDtoWithEndInPast = AddBookingDto.builder()
                .itemId(1L)
                .start(futureOneDay)
                .end(pastOneDay)
                .build();

        createBookingDtoWithoutStart = AddBookingDto.builder()
                .itemId(1L)
                .end(futureTwoDay)
                .build();

        createBookingDtoWithoutEnd = AddBookingDto.builder()
                .itemId(1L)
                .start(futureOneDay)
                .build();

        createBookingDtoWithoutItemId = AddBookingDto.builder()
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
    void exceptionWithCreateBookingWithoutHeaderTest() {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(anyLong(), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void exceptionWithCreateBookingWithStartInPastTest() {
        String jsonBooking = objectMapper.writeValueAsString(createBookingDtoWithStartInPast);

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(anyLong(), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void exceptionWithCreateBookingWithEndInPastTest() {
        String jsonBooking = objectMapper.writeValueAsString(createBookingDtoWithEndInPast);

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(anyLong(), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void exceptionWithCreateBookingWithoutStartTest() {
        String jsonBooking = objectMapper.writeValueAsString(createBookingDtoWithoutStart);

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(anyLong(), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void exceptionWithCreateBookingWithoutEndTest() {
        String jsonBooking = objectMapper.writeValueAsString(createBookingDtoWithoutEnd);

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(anyLong(), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void getExceptionWithCreateBookingWithoutItemId() {
        String jsonBooking = objectMapper.writeValueAsString(createBookingDtoWithoutItemId);

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).create(anyLong(), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBookingTest() {
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
    void exceptionWithApproveBookingWithoutHeaderTest() {
        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
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
    void approveBookingTest() {
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
    void rejectBookingTest() {
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
    void getExceptionWithGetBookingByUserOwnerWithoutHeaderTest() {
        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getBookingByUserOwner(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getBookingWithGetBookingByUserOwnerTest() {
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
    void getExceptionWithGetUserBookingsWithoutHeader() {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetUserBookingsWithWrongState() {
        mockMvc.perform(get("/bookings?state=kek")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: UNSUPPORTED_STATUS"));
        verify(bookingService, never()).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetUserBookingsWithFromLessThen0() {
        mockMvc.perform(get("/bookings?from=-1")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
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

    @Test
    @SneakyThrows
    void getExceptionWithGetUserBookingsWithSizeLessThen1() {
        mockMvc.perform(get("/bookings?size=0")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetUserBookingsWithSizeMoreThen20() {
        mockMvc.perform(get("/bookings?size=21")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getUserBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getBookingWithGetUserBookings() {
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
    void getBookingWithGetUserBookingsWithStateALL() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings?state=ALL")
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
    void getBookingWithGetUserBookingsWithStateCURRENT() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings?state=CURRENT")
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
    void getBookingWithGetUserBookingsWithStatePAST() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings?state=PAST")
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
    void getBookingWithGetUserBookingsWithStateFUTURE() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings?state=FUTURE")
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
    void getBookingWithGetUserBookingsWithStateWAITING() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings?state=WAITING")
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
    void getBookingWithGetUserBookingsWithStateREJECTED() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings?state=REJECTED")
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
    void getExceptionWithGetOwnerBookingsWithoutHeader() {
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetOwnerBookingsWithWrongState() {
        mockMvc.perform(get("/bookings/owner?state=kek")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: UNSUPPORTED_STATUS"));
        verify(bookingService, never()).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetOwnerBookingsWithFromLessThen0() {
        mockMvc.perform(get("/bookings/owner?from=-1")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetOwnerBookingsWithFromMoreThenMaxInt() {
        mockMvc.perform(get("/bookings/owner?from=2147483648")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetOwnerBookingsWithSizeLessThen1() {
        mockMvc.perform(get("/bookings/owner?size=0")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getExceptionWithGetOwnerBookingsWithFromMoreThen20() {
        mockMvc.perform(get("/bookings/owner?size=21")
                        .header(REQUEST_HEADER_USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getBookingWithGetOwnerBookings() {
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

    @Test
    @SneakyThrows
    void getBookingWithGetOwnerBookingsWithStateALL() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner?state=ALL")
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

    @Test
    @SneakyThrows
    void getBookingWithGetOwnerBookingsWithStateCURRENT() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner?state=CURRENT")
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

    @Test
    @SneakyThrows
    void getBookingWithGetOwnerBookingsWithStatePAST() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner?state=PAST")
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

    @Test
    @SneakyThrows
    void getBookingWithGetOwnerBookingsWithStateFUTURE() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner?state=FUTURE")
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

    @Test
    @SneakyThrows
    void getBookingWithGetOwnerBookingsWithStateWAITING() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner?state=WAITING")
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

    @Test
    @SneakyThrows
    void getBookingWithGetOwnerBookingsWithStateREJECTED() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listWith20Bookings);

        mockMvc.perform(get("/bookings/owner?state=REJECTED")
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