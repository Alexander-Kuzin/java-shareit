package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.exceptions.EntityAlreadyExistException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.AddOrUpdateUserDto;
import ru.practicum.shareit.user.dto.GetUserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    private static UserService userService;
    private static UserStorage userStorage;
    private static AddOrUpdateUserDto createUserDto;
    private static AddOrUpdateUserDto updateNameUserDto;
    private static AddOrUpdateUserDto updateEmailUserDto;
    private static GetUserDto getUserDto;
    private static User getUser;
    private static List<User> listOfUser;

    @BeforeAll
    static void beforeAll() {
        createUserDto = AddOrUpdateUserDto.builder()
                .name("name")
                .email("mail@ya.ru")
                .build();

        getUser = User.builder()
                .id(1L)
                .name("name")
                .email("mail@ya.ru")
                .build();

        getUserDto = GetUserDto.builder()
                .id(1L)
                .name("name")
                .email("mail@ya.ru")
                .build();

        updateNameUserDto = AddOrUpdateUserDto.builder()
                .name("newName")
                .build();

        updateEmailUserDto = AddOrUpdateUserDto.builder()
                .email("newMail@ya.ru")
                .build();

        listOfUser = new ArrayList<>();
        for (int i = 1; i < 21; i++) {
            listOfUser.add(getUser.toBuilder().id(i + 1L).build());
        }
    }

    @BeforeEach
    void setUp() {
        userStorage = Mockito.mock(UserStorage.class);
        userService = new UserServiceImpl(userStorage);
    }

    @Test
    void CreateUser() {
        when(userStorage.save(any(User.class)))
                .thenReturn(getUser.toBuilder().build());

        GetUserDto userDto = userService.create(createUserDto);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", getUserDto.getName())
                .hasFieldOrPropertyWithValue("email", getUserDto.getEmail());
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void GetAlreadyExistsExceptionCreateUser() {
        when(userStorage.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("error"));

        final EntityAlreadyExistException exception = Assertions.assertThrows(
                EntityAlreadyExistException.class,
                () -> userService.create(createUserDto));

        assertEquals(String.format("User with mail@ya.ru already exist", createUserDto.getEmail()),
                exception.getMessage());
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void GetNotFoundExceptionWithUpdate() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.update(1L, updateNameUserDto));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void GetAlreadyExistsExceptionUpdateUser() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        when(userStorage.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("error"));

        final EntityAlreadyExistException exception = Assertions.assertThrows(
                EntityAlreadyExistException.class,
                () -> userService.update(1L, updateEmailUserDto));

        assertEquals(String.format("User with newMail@ya.ru already exist", updateEmailUserDto.getEmail()),
                exception.getMessage());
        verify(userStorage, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void UpdateUserName() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        when(userStorage.saveAndFlush(any(User.class)))
                .thenReturn(getUser.toBuilder().name(updateNameUserDto.getName()).build());

        GetUserDto userDto = userService.update(1L, updateNameUserDto);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", updateNameUserDto.getName())
                .hasFieldOrPropertyWithValue("email", getUserDto.getEmail());
        verify(userStorage, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void UpdateUserEmail() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        when(userStorage.saveAndFlush(any(User.class)))
                .thenReturn(getUser.toBuilder().email(updateEmailUserDto.getEmail()).build());

        GetUserDto userDto = userService.update(1L, updateEmailUserDto);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", getUserDto.getName())
                .hasFieldOrPropertyWithValue("email", updateEmailUserDto.getEmail());
        verify(userStorage, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void GetNotFoundExceptionWithDeleteById() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteById(1L));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void DeleteById() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));
        doNothing().when(userStorage).deleteById(anyLong());

        userService.deleteById(1L);

        verify(userStorage, times(1)).deleteById(anyLong());
    }

    @Test
    void GetExceptionGetById() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.getById(1L));

        assertEquals("User with ID 1",
                exception.getMessage());
        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void GetById() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(getUser.toBuilder().build()));

        GetUserDto userDto = userService.getById(1L);

        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", getUserDto.getId())
                .hasFieldOrPropertyWithValue("name", getUserDto.getName())
                .hasFieldOrPropertyWithValue("email", getUserDto.getEmail());
        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void GetAll() {

        when(userStorage.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(listOfUser));
        List<GetUserDto> listUsers = userService.getAll(1, 5);

        assertThat(listUsers)
                .isNotEmpty()
                .hasSize(20)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "name");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "mail@ya.ru");
                });
        verify(userStorage, times(1)).findAll(any(Pageable.class));
    }
}
