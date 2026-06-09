package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImp implements ItemService {

    private final InMemoryItemRepository inMemoryItemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItemDto(Long userId, Item item) {
        log.info("Запрос на создания нового предмета {} , от пользователя {}", item, userId);

        if (item == null) {
            throw new RuntimeException("Item пустой");
        }

        ItemDto createItemDto = itemMapper.toItemDto(item);


        return inMemoryItemRepository.createItemDto(userId, createItemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemsId, ItemDto itemDto) {
        log.info("Получен запрос на обновления item {}", itemsId);
        return inMemoryItemRepository.updateItem(userId, itemsId, itemDto);
    }

    @Override
    public ItemDto getByIdItems(Long userId, Long itemId) {
        log.info("Получен запрос на получение item {}", itemId);
        return inMemoryItemRepository.getByIdItems(userId, itemId);
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        log.info("Получен запрос на все item пользователя с id {}", userId);
        return inMemoryItemRepository.getAllUserItems(userId);
    }

    @Override
    public Collection<ItemDto> searchItem(Long userId, String searchString) {
        log.info("Поиск item по запросу {} , от пользователя {}", searchString, userId);

        if (searchString == null || searchString.isBlank()) {
            log.info("Строка поиска пуста");
            return List.of();
        }

        return inMemoryItemRepository.searchItem(searchString);
    }


}
