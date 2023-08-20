package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.List;

public interface UserService {

    List<GetUserDto> getAll(int from, int size);

    GetUserDto getById(long id);

    GetUserDto create(AddOrUpdateUserDto createUpdateUserDto);

    GetUserDto update(long id, AddOrUpdateUserDto createUpdateUserDto);

    void deleteById(long id);
}