package com.taskmanagment.utils.mapper;

import com.taskmanagment.dto.request.CommentRequest;
import com.taskmanagment.dto.response.CommentResponse;
import com.taskmanagment.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "availability", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "user", ignore = true)
    Comment toEntity(CommentRequest commentRequest);

    @Mapping(target = "taskId", expression = "java(comment.getTask().getTaskId())")
    @Mapping(target = "authorId", expression = "java(comment.getUser().getUserId())")
    CommentResponse toResponse(Comment comment);
}
