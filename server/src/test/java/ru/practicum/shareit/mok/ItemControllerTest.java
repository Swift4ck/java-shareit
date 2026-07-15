package ru.practicum.shareit.mok;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItem() throws Exception {
        long userId = 1L;
        ItemDto inputDto = new ItemDto(null, "test", "opisanie", true, null, null);
        ItemDto savedDto = new ItemDto(1L, "test", "opisanie", true, null, userId);

        when(itemService.createItemDto(eq(userId), any(ItemDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem() throws Exception {
        long userId = 1L;
        long itemId = 10L;
        ItemDto inputDto = new ItemDto(null, "test", null, false, null, null);
        ItemDto updatedDto = new ItemDto(itemId, "test+", "opisanie", false, 2L, userId);

        when(itemService.updateItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(updatedDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getById() throws Exception {
        long userId = 1L;
        long itemId = 10L;
        ItemDto itemDto = new ItemDto(itemId, "test", "opisanie", true, null, userId);

        when(itemService.getByIdItems(userId, itemId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    void getAllUserItems() throws Exception {
        long userId = 1L;
        List<ItemDto> items = List.of(
                new ItemDto(1L, "test1", "opisanie1", true, null, userId),
                new ItemDto(2L, "test2", "opisanie2", true, null, userId)
        );

        when(itemService.getAllUserItems(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("test1"));
    }

    @Test
    void addComment() throws Exception {
        long userId = 1L;
        long itemId = 10L;

        CommentDto inputComment = new CommentDto(null, "text", null, null, null, null);
        CommentDto savedComment = new CommentDto(1L, "text", itemId, "автор", userId, null);

        when(itemService.addComment(eq(userId), eq(itemId), any(CommentDto.class)))
                .thenReturn(savedComment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.authorName").value("автор"));
    }


}
