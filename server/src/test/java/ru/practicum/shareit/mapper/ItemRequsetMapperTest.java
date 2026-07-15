package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequsetMapperTest {

    @Test
    public void toItemRequestDto() {

        User user = new User(1L, "васек", "vscx@mail.com");
        ItemRequest request = new ItemRequest();
        request.setId(10L);
        request.setDescription("h");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.of(2025, 1, 1, 12, 0));


        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);

        assertEquals(10L, dto.getId());
        assertEquals("h", dto.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), dto.getCreated());
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    public void toItemRequestDtoWithItemsShouldReturnDtoWithGivenItems() {

        User user = new User(1L, "васек", "vscx@mail.com");
        ItemRequest request = new ItemRequest();
        request.setId(20L);
        request.setDescription("Запрос");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        RequestDto itemDto1 = RequestDto.builder().id(1L).name("Item1").otherId(100L).build();
        RequestDto itemDto2 = RequestDto.builder().id(2L).name("Item2").otherId(200L).build();
        List<RequestDto> items = List.of(itemDto1, itemDto2);


        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request, items);


        assertEquals(20L, dto.getId());
        assertEquals(2, dto.getItems().size());
        assertEquals("Item1", dto.getItems().get(0).getName());
        assertEquals("Item2", dto.getItems().get(1).getName());
    }

    @Test
    public void toItemRequest() {

        User user = new User(1L, "васек", "vscx@mail.com");
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("про");
        dto.setCreated(LocalDateTime.of(2025, 6, 10, 15, 30));

        ItemRequest entity = ItemRequestMapper.toItemRequest(dto, user);


        assertNotNull(entity);
        assertEquals("про", entity.getDescription());
        assertSame(user, entity.getRequestor());
        assertEquals(LocalDateTime.of(2025, 6, 10, 15, 30), entity.getCreated());
    }

    @Test
    public void toRequestDto() {

        Item item = new Item();
        item.setId(100L);
        item.setName("пк");
        item.setOtherId(33L);


        RequestDto requestDto = ItemRequestMapper.toRequestDto(item);


        assertEquals(100L, requestDto.getId());
        assertEquals("пк", requestDto.getName());
        assertEquals(33L, requestDto.getOtherId());
    }

}
