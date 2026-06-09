package ru.practicum.shareit.item.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final InMemoryUserRepository inMemoryUserRepository;

    private final Map<Long, ItemDto> items = new HashMap<>();
    private long nextId = 1L;

    @Override
    public ItemDto createItemDto(Long userId, ItemDto itemDto) {

        User user = inMemoryUserRepository.getByUserId(userId);

        if (user == null) {
            throw new NotFoundException("Пользователя не существует");
        }

        if (itemDto.getId() == null) {
            itemDto.setId(nextId++);
        }

        if (itemDto.getOtherId() == null) {
            itemDto.setOtherId(userId);
        }

        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(true);
        }

        items.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemsId, ItemDto itemDto) {

        ItemDto updateItemDto = items.get(itemsId);

        if (updateItemDto == null) {
            throw new NotFoundException("Item не существует");
        }

        if (!updateItemDto.getOtherId().equals(userId)) {
            throw new NotFoundException("Item не принадлежит вам");
        }

        updateItemDto.setName(itemDto.getName());
        updateItemDto.setDescription(itemDto.getDescription());
        updateItemDto.setAvailable(itemDto.getAvailable());

        return updateItemDto;
    }

    @Override
    public ItemDto getByIdItems(Long userId, Long itemId) {
        ItemDto itemDto = items.get(itemId);

        if (itemDto == null) {
            throw new NotFoundException("Item не существует");
        }

        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        return items.values().stream()
                .filter(itemDto -> itemDto.getOtherId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItem(String searchString) {
        String search = searchString.toLowerCase();

        return items.values().stream()
                .filter(itemDto -> Boolean.TRUE.equals(itemDto.getAvailable()))
                .filter(itemDto -> {
                    String name = itemDto.getName();
                    String description = itemDto.getDescription();
                    return (name != null && name.toLowerCase().contains(search)) ||
                            (description != null && description.toLowerCase().contains(search));
                })
                .collect(Collectors.toList());
    }

}
