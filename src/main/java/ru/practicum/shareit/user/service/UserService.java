package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

public interface UserService {

    User createUser(User user);

    User updateUser(Long userId, User user);

    User getByUserId(Long userId);

    void deleteUser(Long userId);
}
