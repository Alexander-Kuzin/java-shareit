package ru.practicum.shareit.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public static GetUserDto toGetUserDtoFromUser(User user) {
        return GetUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUserFromAddOrUpdateUserDto(AddOrUpdateUserDto addOrUpdateUserDto) {
        return User.builder()
                .name(addOrUpdateUserDto.getName())
                .email(addOrUpdateUserDto.getEmail())
                .build();
    }

    public static GetBookingUserDto toGetBookingUserDtoFromUser(User user) {
        return GetBookingUserDto.builder()
                .id(user.getId())
                .build();
    }
}