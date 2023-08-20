package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Marker;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<GetUserDto> getAll(@RequestParam(defaultValue = "0")
                                   @Min(0) @Max(Integer.MAX_VALUE) int from,
                                   @RequestParam(defaultValue = "20")
                                   @Min(1) @Max(20) int size) {
        return userService.getAll(from, size);
    }

    @GetMapping("/{userId}")
    public GetUserDto getById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public GetUserDto create(@RequestBody
                             @Validated(Marker.OnCreate.class) AddOrUpdateUserDto createUpdateUserDto) {
        return userService.create(createUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public GetUserDto update(@PathVariable long userId,
                             @RequestBody
                             @Validated(Marker.OnUpdate.class) AddOrUpdateUserDto createUpdateUserDto) {
        return userService.update(userId, createUpdateUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}