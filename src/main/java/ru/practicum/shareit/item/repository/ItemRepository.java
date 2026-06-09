package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemRepository {

    ItemDto createItemDto(Long userId, ItemDto itemDto);

    ItemDto getByIdItems(Long userId, Long itemId);

    ItemDto updateItem(Long userId, Long itemsId, ItemDto itemDto);

    Collection<ItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItem(String searchString);

}
