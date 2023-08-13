package ru.practicum.shareit.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public static GetCommentDto toGetCommentDtoFromComment(Comment comment) {
        return GetCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment toCommentFromCreateCommentDto(AddCommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }
}