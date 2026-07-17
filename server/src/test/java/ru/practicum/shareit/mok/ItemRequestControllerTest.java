package ru.practicum.shareit.mok;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createRequest() throws Exception {
        long userId = 1L;

        ItemRequestDto inputDto = ItemRequestDto.builder()
                .description("test")
                .build();

        ItemRequestDto savedDto = ItemRequestDto.builder()
                .id(1L)
                .description("test")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.create(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(savedDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("test"));
    }

    @Test
    void getAllRequest() throws Exception {
        long userId = 1L;
        ItemRequestDto dto1 = ItemRequestDto.builder()
                .id(1L).description("Запрос 1").created(LocalDateTime.now()).items(Collections.emptyList()).build();
        ItemRequestDto dto2 = ItemRequestDto.builder()

                .id(2L).description("Запрос 2").created(LocalDateTime.now()).items(Collections.emptyList()).build();
        List<ItemRequestDto> list = List.of(dto1, dto2);

        when(itemRequestService.getAllRequestor(userId)).thenReturn(list);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getAll() throws Exception {
        long userId = 1L;

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(3L).description("Запрос").created(LocalDateTime.now()).items(Collections.emptyList()).build();

        List<ItemRequestDto> list = List.of(dto);

        when(itemRequestService.getAll(userId)).thenReturn(list);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    void getById() throws Exception {
        long requestId = 5L;
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(requestId)
                .description("запрос")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getById(requestId)).thenReturn(dto);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("запрос"));
    }

}
