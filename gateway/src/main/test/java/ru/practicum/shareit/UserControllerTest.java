package ru.practicum.shareit;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.UserDto;


import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUser() throws Exception {

        UserDto inputDto = new UserDto(null, "testov", "test@yandex.su");

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("body");

        when(userClient.createUser(any(UserDto.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("body"));
    }

    @Test
    public void createUserEmptyName() throws Exception {

        UserDto inputDto = new UserDto(null, "", "test@yandex.su");


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserEmptyEmail() throws Exception {

        UserDto inputDto = new UserDto(null, "testov", "");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_ValidRequest_ReturnsClientResponse() throws Exception {

        long userId = 1L;

        UserDto inputDto = new UserDto(null, "testov", "test@yandex.su");

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("update");

        when(userClient.updateUser(eq(userId), any(UserDto.class))).thenReturn(clientResponse);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("update"));
    }

    @Test
    public void getUserById() throws Exception {
        long userId = 1L;

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("body");

        when(userClient.getByUserId(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("body"));
    }

    @Test
    public void deleteUser() throws Exception {

        long userId = 1L;

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("body");

        when(userClient.deleteUser(userId)).thenReturn(clientResponse);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("body"));
    }


}
