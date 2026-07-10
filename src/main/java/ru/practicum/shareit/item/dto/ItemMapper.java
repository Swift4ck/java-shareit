package ru.practicum.shareit.item.dto;


import lombok.AccessLevel;
import lombok.Data;


import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest(),
                item.getOtherId()
        );
    }

    public static Item toItem(ItemDto itemDto, ItemRequest request) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .otherId(itemDto.getOtherId())
                .request(request)
                .build();
    }
}


