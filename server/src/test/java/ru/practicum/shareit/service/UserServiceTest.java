package ru.practicum.shareit.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImp;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImp userServiceImp;

    private User user;

    @BeforeEach
    public void user() {
        user = new User(1L, "Test", "testovi@pochta.com");
    }

    @Test
    public void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userServiceImp.createUser(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals("testovi@pochta.com", result.getEmail());

        verify(userRepository).save(user);
    }


    @Test
    void updateUser() {
        User updated = new User(null, "Test", "testovi@pochta.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userServiceImp.updateUser(1L, updated);

        assertEquals("Test", result.getName());
        assertEquals("testovi@pochta.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserOnlyNameProvided() {
        User updated = new User(null, "Test", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userServiceImp.updateUser(1L, updated);

        assertEquals("Test", result.getName());
        assertEquals("testovi@pochta.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserOnlyEmailProvided() {
        User updated = new User(null, null, "testovi@pochta.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userServiceImp.updateUser(1L, updated);

        assertEquals("Test", result.getName());
        assertEquals("testovi@pochta.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImp.updateUser(99L, user));
    }


    @Test
    void getByUserIdUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userServiceImp.getByUserId(1L);

        assertEquals(user, result);
    }

    @Test
    void getByUserIdUserNotFound() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImp.getByUserId(99L));
    }


    @Test
    void deleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userServiceImp.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserUserNotFound() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImp.deleteUser(99L));
    }

}
