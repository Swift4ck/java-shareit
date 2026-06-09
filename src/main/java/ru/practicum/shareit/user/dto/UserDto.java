package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank
    private String name;


    public UserDto(String name) {
        this.name = name;
    }
}
