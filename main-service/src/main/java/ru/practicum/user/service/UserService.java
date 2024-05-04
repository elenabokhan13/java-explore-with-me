package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);

    Collection<UserDto> getUser(List<Long> ids, int from, int size);
}
