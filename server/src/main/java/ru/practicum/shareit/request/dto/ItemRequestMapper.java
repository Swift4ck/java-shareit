package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return toItemRequestDto(itemRequest, List.of());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<RequestDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(itemRequestDto.getCreated());

        return itemRequest;
    }

    public static RequestDto toRequestDto(Item item) {
        return RequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .otherId(item.getOtherId())
                .build();
    }


}


