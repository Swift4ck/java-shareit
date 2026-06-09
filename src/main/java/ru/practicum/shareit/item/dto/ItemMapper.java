package ru.practicum.shareit.item.dto;


import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Data
@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest()
        );
    }

}
