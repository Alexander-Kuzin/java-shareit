package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.markers.Marker;
import ru.practicum.shareit.services.UserService;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
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
    @Validated(Marker.OnCreate.class)
    public GetUserDto add(@RequestBody
                          @Valid AddOrUpdateUserDto addOrUpdateUserDto) {
        return userService.add(addOrUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public GetUserDto update(@PathVariable long userId,
                             @RequestBody
                             @Validated(Marker.OnUpdate.class) AddOrUpdateUserDto addOrUpdateUserDto) {
        return userService.update(userId, addOrUpdateUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}