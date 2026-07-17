package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    UserDto getByUserId(Long userId);

    void deleteUser(Long user);
}
