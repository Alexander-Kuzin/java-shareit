package ru.practicum.shareit.storages;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User getById(long id);

    User add(User user);

    User update(User user);

    void deleteById(long id);
}