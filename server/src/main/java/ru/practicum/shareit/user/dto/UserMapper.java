package ru.practicum.shareit.user.dto;


import ru.practicum.shareit.user.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        UserDto dto = new UserDto(user.getName(), user.getEmail());
        dto.setId(user.getId());
        return dto;
    }

    public static User toUser(UserDto userDto) {

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

}
