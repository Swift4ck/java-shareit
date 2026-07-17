package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "items")
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;


    private String description;

    private Boolean available;

    @Column(name = "other_id")
    private Long otherId;

    @Column(name = "request_id")
    private Long requestId;

}
