package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotations.ValuesAllowedConstraint;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<GetBookingDto> getUserBookings(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                               @ValuesAllowedConstraint(propName = "state",
                                                       values = {"all",
                                                               "current",
                                                               "past",
                                                               "future",
                                                               "waiting",
                                                               "rejected"},
                                                       message = "Unknown state: UNSUPPORTED_STATUS")
                                               @RequestParam(defaultValue = "all") String state,
                                               @RequestParam(defaultValue = "0")
                                               @Min(0) @Max(Integer.MAX_VALUE) int from,
                                               @RequestParam(defaultValue = "20")
                                               @Min(1) @Max(20) int size) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<GetBookingDto> getOwnerBookings(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                @ValuesAllowedConstraint(propName = "state",
                                                        values = {"all",
                                                                "current",
                                                                "past",
                                                                "future",
                                                                "waiting",
                                                                "rejected"},
                                                        message = "Unknown state: UNSUPPORTED_STATUS")
                                                @RequestParam(defaultValue = "all")
                                                String state,
                                                @RequestParam(defaultValue = "0")
                                                @Min(0) int from,
                                                @RequestParam(defaultValue = "20")
                                                @Min(1) @Max(20) int size) {
        return bookingService.getOwnerBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public GetBookingDto getBookingByUserOwner(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                               @PathVariable long bookingId) {
        return bookingService.getBookingByUserOwner(userId, bookingId);
    }

    @PostMapping
    public GetBookingDto create(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                @RequestBody
                                @Valid AddBookingDto addBookingDto) {
        return bookingService.create(userId, addBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public GetBookingDto approveBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                        @PathVariable long bookingId,
                                        @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }
}