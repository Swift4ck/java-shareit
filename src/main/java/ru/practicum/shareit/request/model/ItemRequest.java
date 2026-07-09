package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String description;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "requestor_id")
    private User requestor;

    private LocalDateTime created;
}
