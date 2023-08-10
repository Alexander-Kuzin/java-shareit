package ru.practicum.shareit.services;

import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.List;

public interface UserService {
    List<GetUserDto> getAll();

    GetUserDto getById(long id);

    GetUserDto add(AddOrUpdateUserDto addOrUpdateUserDto);

    GetUserDto update(long id, AddOrUpdateUserDto addOrUpdateUserDto);

    void deleteById(long id);
}