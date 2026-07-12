package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        log.info("Получен запрос на создания пользователя {}", user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        log.info("Получен запрос на обновления пользователя {}", user);

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        if (user.getName() != null) {
            findUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            findUser.setEmail(user.getEmail());
        }

        return userRepository.save(findUser);
    }

    @Override
    public User getByUserId(Long userId) {
        log.info("Получен запрос на получения пользователя {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Получен запрос на удаления пользователя {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        userRepository.delete(user);
    }
}
