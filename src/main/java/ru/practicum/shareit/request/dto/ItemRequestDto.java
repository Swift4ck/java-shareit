package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests
 */
public class ItemRequestDto {

    private long id;

    @NotBlank
    private String description;

    private User requestor;

    private LocalDateTime created;
}
