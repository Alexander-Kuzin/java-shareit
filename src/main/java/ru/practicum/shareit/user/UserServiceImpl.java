package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.exceptions.EntityAlreadyExistException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utilities.Constants.SORT_BY_ID_ASC;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Transactional(readOnly = true)
    @Override
    public List<GetUserDto> getAll() {
        return userStorage.findAll(SORT_BY_ID_ASC)
                .stream()
                .map(UserMapper::toGetUserDtoFromUser)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GetUserDto getById(long id) {
        return UserMapper.toGetUserDtoFromUser(userStorage.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", id))));
    }

    @Override
    public GetUserDto create(AddOrUpdateUserDto createUpdateUserDto) {
        try {
            return UserMapper.toGetUserDtoFromUser(
                    userStorage.save(UserMapper.toUserFromAddOrUpdateUserDto(createUpdateUserDto)));
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistException(
                    String.format("User with %s already exist", createUpdateUserDto.getEmail()));
        }
    }

    @Override
    public GetUserDto update(long id, AddOrUpdateUserDto createUpdateUserDto) {
        User user = userStorage.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", id)));

        if (createUpdateUserDto.getName() != null && !createUpdateUserDto.getName().isBlank()) {
            user.setName(createUpdateUserDto.getName());
        }

        if (createUpdateUserDto.getEmail() != null && !createUpdateUserDto.getEmail().isBlank()) {
            user.setEmail(createUpdateUserDto.getEmail());
        }

        try {
            return UserMapper.toGetUserDtoFromUser(
                    userStorage.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistException(String.format(
                    "User with %s already exist", createUpdateUserDto.getEmail()));
        }
    }

    @Override
    public void deleteById(long id) {
        userStorage.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with ID %s", id)));
        userStorage.deleteById(id);
    }
}