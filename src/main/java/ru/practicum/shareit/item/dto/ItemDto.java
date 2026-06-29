package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@Getter
@Setter
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private Long otherId;

    private String request;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;


    public ItemDto(Long id, String name, String description, Boolean available, String request, Long otherId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
        this.otherId = otherId;
    }
}
