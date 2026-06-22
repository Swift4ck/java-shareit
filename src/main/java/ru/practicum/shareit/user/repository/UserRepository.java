package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

public interface UserRepository {

    User createUser(User user);

    boolean duplicateEmail(User user);

    User updateUser(Long userId, User user);

    User getByUserId(Long userId);

    void deleteUser(Long userId);
}
