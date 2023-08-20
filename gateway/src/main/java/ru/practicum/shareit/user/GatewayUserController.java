package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Marker;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class GatewayUserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(defaultValue = "0")
                                         @Min(0) @Max(Integer.MAX_VALUE) int from,
                                         @RequestParam(defaultValue = "20")
                                         @Min(1) @Max(20) int size) {
        return client.getAll(from, size);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        return client.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody
                                         @Validated(Marker.OnCreate.class) AddOrUpdateUserDto addOrUpdateUserDto) {
        return client.create(addOrUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                                         @RequestBody
                                         @Validated(Marker.OnUpdate.class) AddOrUpdateUserDto addOrUpdateUserDto) {
        return client.update(userId, addOrUpdateUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable long userId) {
        return client.deleteById(userId);
    }
}