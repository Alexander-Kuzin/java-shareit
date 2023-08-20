package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.utilities.Constants.REQUEST_HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public GetItemRequestDto createRequest(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                           @RequestBody
                                           @Valid AddItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<GetItemRequestDto> getAllRequestsByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemRequestService.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getAllRequests(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                  @RequestParam(defaultValue = "0")
                                                  @Min(0) @Max(Integer.MAX_VALUE) int from,
                                                  @RequestParam(defaultValue = "20")
                                                  @Min(1) @Max(20) int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto getRequestById(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                            @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}