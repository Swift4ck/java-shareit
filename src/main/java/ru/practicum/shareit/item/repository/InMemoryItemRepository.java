package ru.practicum.shareit.item.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final UserRepository inMemoryUserRepository;

    private final Map<Long, Item> items = new HashMap<>();
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

        Item item = ItemMapper.toItem(itemDto);

        items.put(item.getId(), item);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemsId, ItemDto itemDto) {

        Item updateItem = items.get(itemsId);

        if (updateItem == null) {
            throw new NotFoundException("Item не существует");
        }

        ItemDto updateItemDto = ItemMapper.toItemDto(updateItem);

        if (!updateItemDto.getOtherId().equals(userId)) {
            throw new NotFoundException("Item не принадлежит вам");
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()){
            updateItemDto.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()){
            updateItemDto.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null){
            updateItemDto.setAvailable(itemDto.getAvailable());
        }

        return updateItemDto;
    }

    @Override
    public ItemDto getByIdItems(Long userId, Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Item не существует");
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {


        return items.values().stream()
                .filter(itemDto -> itemDto.getOtherId().equals(userId))
                .map(ItemMapper::toItemDto)
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
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
