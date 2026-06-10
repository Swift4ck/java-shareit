package ru.practicum.shareit.item.dto;


import lombok.AccessLevel;
import lombok.Data;


import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

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

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOtherId(),
                itemDto.getRequest()
        );
    }

}
