package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.Comment;

@Data
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthorName(),
                comment.getAuthorId(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getItemId(),
                commentDto.getAuthorName(),
                commentDto.getAuthorId(),
                commentDto.getCreated()
        );
    }

}
