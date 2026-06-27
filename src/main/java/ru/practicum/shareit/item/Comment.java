package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "created_time")
    private LocalDateTime created;

}
