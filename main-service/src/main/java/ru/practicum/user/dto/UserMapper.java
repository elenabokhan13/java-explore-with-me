package ru.practicum.user.dto;

import ru.practicum.user.model.User;

public class UserMapper {
    public static UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User dtoToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static UserShortDto userShortToDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
