package ru.practicum.shareit.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
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
        UserDto inputDto = new UserDto("Test", "testovi@pochta.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userServiceImp.createUser(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals("testovi@pochta.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }


    @Test
    public void updateUser() {
        UserDto updateDto = new UserDto("NewName", "new@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userServiceImp.updateUser(1L, updateDto);

        assertEquals("NewName", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserOnlyNameProvided() {
        UserDto updateDto = new UserDto("NewName", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userServiceImp.updateUser(1L, updateDto);

        assertEquals("NewName", result.getName());
        assertEquals("testovi@pochta.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserOnlyEmailProvided() {
        UserDto updateDto = new UserDto(null, "new@mail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userServiceImp.updateUser(1L, updateDto);

        assertEquals("Test", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImp.updateUser(99L, new UserDto("name", "email")));
    }


    @Test
    void getByUserIdUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userServiceImp.getByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals("testovi@pochta.com", result.getEmail());
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
