package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;


import java.util.Collection;

public interface ItemService {

    ItemDto createItemDto(Long userId, ItemDto item);

    ItemDto getByIdItems(Long userId, Long itemId);

    ItemDto updateItem(Long userId, Long itemsId, ItemDto itemDto);

    Collection<ItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItem(Long userId, String searchString);

}
