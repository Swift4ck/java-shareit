package ru.practicum.shareit.mapper;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {


    @Test
    public void toUserDto() {
        User user = new User(1L, "Вася пупкин", "pupok@vasin.hs");

        UserDto dto = UserMapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals("Вася пупкин", dto.getName());
    }

}
