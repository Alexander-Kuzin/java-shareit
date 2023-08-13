package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotations.ValuesAllowedConstraint;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;

import javax.validation.Valid;
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
                                               @RequestParam(defaultValue = "all") String state) {
        return bookingService.getUserBookings(userId, state);
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
                                                @RequestParam(defaultValue = "all") String state) {
        return bookingService.getOwnerBookings(userId, state);
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