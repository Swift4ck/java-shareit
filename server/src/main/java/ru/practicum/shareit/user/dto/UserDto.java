package ru.practicum.shareit.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {


    private String name;


    public UserDto(String name) {
        this.name = name;
    }
}
