package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;


import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    public void toItemDto() {
        Item item = new Item(5L, "вещь", "такая", true, 1L, 100L);


        ItemDto dto = ItemMapper.toItemDto(item);


        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("вещь", dto.getName());
        assertEquals("такая", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(1L, dto.getOtherId());
        assertEquals(100L, dto.getRequestId());
    }

    @Test
    public void toItem() {

        ItemDto dto = new ItemDto(5L, "вещь", "такая", false, 321L, 123L);


        Item item = ItemMapper.toItem(dto);


        assertNotNull(item);
        assertEquals(5L, item.getId());
        assertEquals("вещь", item.getName());
        assertEquals("такая", item.getDescription());
        assertFalse(item.getAvailable());
        assertEquals(321L, item.getRequestId());
        assertEquals(123L, item.getOtherId());
    }

}
