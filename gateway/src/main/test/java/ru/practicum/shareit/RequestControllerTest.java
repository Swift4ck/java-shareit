package ru.practicum.shareit;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.RequestController;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createRequest() throws Exception {

        long userId = 1L;

        ItemRequestDto inputDto = new ItemRequestDto(0, "нужна дрель", null, null);
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("request created");
        when(requestClient.create(eq(userId), any(ItemRequestDto.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("request created"));
    }

    @Test
    public void getAllRequestor() throws Exception {

        long userId = 1L;

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("body");

        when(requestClient.getAllRequestor(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("body"));
    }


    @Test
    public void getAll() throws Exception {

        long userId = 1L;

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("body");

        when(requestClient.getAll(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("body"));
    }

    @Test
    public void getById() throws Exception {
        long requestId = 1L;

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("body");

        when(requestClient.getById(requestId)).thenReturn(clientResponse);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(content().string("body"));
    }


}
