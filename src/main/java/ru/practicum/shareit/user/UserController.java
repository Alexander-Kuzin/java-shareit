package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Marker;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<GetUserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public GetUserDto getById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public GetUserDto create(@RequestBody @Validated(Marker.OnCreate.class) AddOrUpdateUserDto createUpdateUserDto) {
        return userService.create(createUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public GetUserDto update(@PathVariable long userId,
                             @RequestBody @Validated(Marker.OnUpdate.class) AddOrUpdateUserDto createUpdateUserDto) {
        return userService.update(userId, createUpdateUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}