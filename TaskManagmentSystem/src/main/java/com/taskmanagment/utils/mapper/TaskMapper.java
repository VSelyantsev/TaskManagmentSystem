package com.taskmanagment.utils.mapper;

import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.dto.response.CommentResponse;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {


    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "user", ignore = true)
    Task toEntity(TaskRequest taskRequest);


    @Mapping(target = "comments", ignore = true)
    TaskResponse toResponse(Task task);

    @Mapping(target = "authorId", expression = "java(task.getUser().getUserId())")
    @Mapping(target = "taskId", expression = "java(task.getTaskId())")
    @Mapping(target = "comments", expression = "java(TaskMapper.getResponses(task))")
    TaskResponse toResponseWithComments(Task task);

    static List<CommentResponse> getResponses(Task task) {
        return task.getComments().stream()
                .map(comment -> CommentResponse
                        .builder()
                        .taskId(task.getTaskId())
                        .authorId(comment.getUser().getUserId())
                        .body(comment.getBody())
                        .availability(comment.getAvailability())
                        .commentId(comment.getCommentId())
                        .build()
                ).collect(Collectors.toList());
    }
}
