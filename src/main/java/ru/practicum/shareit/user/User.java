package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {

    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    @NotNull
    private String email;


}
