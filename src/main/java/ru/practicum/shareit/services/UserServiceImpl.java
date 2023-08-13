package ru.practicum.shareit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.storages.UserStorage;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<GetUserDto> getAll() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::toGetUserDtoFromUser)
                .collect(Collectors.toList());
    }

    @Override
    public GetUserDto getById(long id) {
        return UserMapper.toGetUserDtoFromUser(userStorage.getById(id));
    }

    @Override
    public GetUserDto add(AddOrUpdateUserDto addOrUpdateUserDto) {
        return UserMapper.toGetUserDtoFromUser(
                userStorage.add(UserMapper.toUserFromAddOrUpdateUserDto(addOrUpdateUserDto))
        );
    }

    @Override
    public GetUserDto update(long id, AddOrUpdateUserDto addOrUpdateUserDto) {
        User user = userStorage.getById(id);

        if (addOrUpdateUserDto.getName() != null && !addOrUpdateUserDto.getName().isBlank()) {
            user.setName(addOrUpdateUserDto.getName());
        }

        if (addOrUpdateUserDto.getEmail() != null && !addOrUpdateUserDto.getEmail().isBlank()) {
            user.setEmail(addOrUpdateUserDto.getEmail());
        }

        return UserMapper.toGetUserDtoFromUser(userStorage.update(user));
    }

    @Override
    public void deleteById(long id) {
        userStorage.deleteById(id);
    }
}