package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {


    @Test
    public void toCommentDto() {

        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment(1L, "круто", 10L, "name", 2L, now);

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("круто", dto.getText());
        assertEquals(10L, dto.getItemId());
        assertEquals("name", dto.getAuthorName());
        assertEquals(2L, dto.getAuthorId());
        assertEquals(now, dto.getCreated());
    }

    @Test
    public void toComment() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = new CommentDto(2L, "не круто", 20L, "sda", 3L, now);

        Comment comment = CommentMapper.toComment(dto);

        assertNotNull(comment);
        assertEquals(2L, comment.getId());
        assertEquals("не круто", comment.getText());
        assertEquals(20L, comment.getItemId());
        assertEquals("sda", comment.getAuthorName());
        assertEquals(3L, comment.getAuthorId());
        assertEquals(now, comment.getCreated());
    }

}
