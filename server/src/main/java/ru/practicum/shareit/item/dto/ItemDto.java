package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@Getter
@Setter
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long otherId;

    @JsonProperty("requestId")
    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;


    public ItemDto(Long id, String name, String description, Boolean available, Long requestId, Long otherId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
        this.otherId = otherId;
    }
}
