package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.annotations.StartBeforeEndDateValid;
import ru.practicum.shareit.annotations.ValuesAllowedConstraint;
import ru.practicum.shareit.booking.dto.AddBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class GatewayBookingController {
    private final BookingClient client;

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
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

        return client.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
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
                                                   @Min(0) int from,
                                                   @RequestParam(defaultValue = "20")
                                                   @Min(1) @Max(20) int size) {
        return client.getOwnerBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByUserOwner(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                        @PathVariable long bookingId) {
        return client.getBookingByUserOwner(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                         @RequestBody
                                         @Valid
                                         @StartBeforeEndDateValid(message = "End date can't be before start date")
                                         AddBookingDto createBookingDto) {
        return client.create(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam Boolean approved) {
        return client.approveBooking(userId, bookingId, approved);
    }
}
