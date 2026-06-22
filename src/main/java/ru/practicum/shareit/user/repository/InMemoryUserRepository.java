package ru.practicum.shareit.user.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1L;


    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }

        if (duplicateEmail(user)) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean duplicateEmail(User user) {
        return users.values().stream()
                .anyMatch(user1 -> Objects.equals(user1.getEmail(), user.getEmail()));
    }

    @Override
    public User updateUser(Long userId, User user) {

        User updateUser = users.get(userId);

        if (updateUser == null) {
            throw new NotFoundException("Пользователя такого не существует");
        }

        if (user.getEmail() != null && !user.getEmail().equals(updateUser.getEmail())) {
            if (duplicateEmail(user)) {
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
            updateUser.setEmail(user.getEmail());
        }

        if (user.getName() != null && !user.getName().isEmpty()) {
            updateUser.setName(user.getName());
        }


        return updateUser;
    }

    @Override
    public User getByUserId(Long userId) {
        User getUser = users.get(userId);

        if (getUser == null) {
            throw new NotFoundException("Пользователя не существует");
        }

        log.info("Запро на получения пользователя {}", getUser);
        return getUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

}
