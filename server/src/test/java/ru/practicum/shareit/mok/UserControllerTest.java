package ru.practicum.shareit.mok;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUser() throws Exception {

        User enterUser = new User(null, "Тест тестович", "test@yandex.ru");

        User savedUser = new User(1L, "Тест тестович", "test@yandex.ru");

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тест тестович"))
                .andExpect(jsonPath("$.email").value("test@yandex.ru"));

    }

    @Test
    public void getUserId() throws Exception {
        User user = new User(1L, "Тест тестович", "test@yandex.ru");

        when(userService.getByUserId(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тест тестович"));
    }

    @Test
    public void updateUser() throws Exception {

        User updateUser = new User(1L, "Тест тестович", "test@yandex.ru");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updateUser);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Тест тестович\", \"email\":\"test@yandex.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тест тестович"));

    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }

}
