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
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;


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
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createItem() throws Exception {
        long userId = 1L;
        ItemDto inputDto = new ItemDto(null, "item", "desc", true,
                null, null, null, null, null);
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("item created");

        when(itemClient.createItem(eq(userId), any(ItemDto.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("item created"));
    }

    @Test
    public void updateItem() throws Exception {
        long userId = 1L;
        long itemId = 10L;

        ItemDto inputDto = new ItemDto(null, "newName", null, false,
                null, null, null, null, null);

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("updated");

        when(itemClient.updateItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(clientResponse);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("updated"));
    }

    @Test
    void getItemById() throws Exception {
        long userId = 1L;
        long itemId = 10L;

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("item");

        when(itemClient.getByIdItems(userId, itemId)).thenReturn(clientResponse);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("item"));
    }


    @Test
    public void getAllUserItems() throws Exception {
        long userId = 1L;
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("allItems");
        when(itemClient.getAllUserItems(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("allItems"));
    }

    @Test
    public void searchItem() throws Exception {
        long userId = 1L;
        String text = "poisc";

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("poisc");

        when(itemClient.searchItem(userId, text)).thenReturn(clientResponse);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().string("poisc"));
    }

    @Test
    public void searchItemNullSearch() throws Exception {
        long userId = 1L;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    @Test
    public void addComment() throws Exception {
        long userId = 1L;
        long itemId = 10L;

        CommentDto inputComment = new CommentDto(null, "nice", null, null);

        ResponseEntity<Object> clientResponse = ResponseEntity.ok("saved");

        when(itemClient.addComment(eq(userId), eq(itemId), any(CommentDto.class))).thenReturn(clientResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputComment)))
                .andExpect(status().isOk())
                .andExpect(content().string("saved"));
    }

}
