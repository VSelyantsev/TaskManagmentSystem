package com.taskmanagment.service.impl;

import com.taskmanagment.dto.request.CommentRequest;
import com.taskmanagment.dto.response.CommentResponse;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.exception.NotFoundException;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.model.Comment;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.repository.CommentRepository;
import com.taskmanagment.service.CommentService;
import com.taskmanagment.service.TaskService;
import com.taskmanagment.service.UserService;
import com.taskmanagment.utils.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final TaskService taskService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponse create(UUID taskId, CommentRequest commentRequest) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        TaskResponse existingTask = taskService.findTaskById(taskId);
        Comment comment = commentMapper.toEntity(commentRequest);
        comment.setTask(Task.builder().taskId(existingTask.getTaskId()).build());
        comment.setUser(User.builder().userId(authUser.getUserId()).build());
        comment.setAvailability(ActivityStatus.AVAILABLE);
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponse findCommentById(UUID commentId) {
        return commentMapper.toResponse(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundKeyException(commentId))
        );
    }

    @Override
    public List<CommentResponse> findAll() {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User not Found"));

        return commentRepository.findAll()
                .stream()
                .filter(comment -> comment.getAvailability().equals(ActivityStatus.AVAILABLE))
                .filter(comment -> comment.getUser().getUserId().equals(authUser.getUserId()))
                .map(commentMapper::toResponse)
                .toList();
    }

    @Override
    public CommentResponse update(UUID commentId, CommentRequest commentRequest) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User not Found"));

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundKeyException(commentId));

        if (existingComment.getUser().getUserId().equals(authUser.getUserId())) {
            existingComment.setBody(commentRequest.getBody());
            commentRepository.save(existingComment);
        }

        return commentMapper.toResponse(existingComment);
    }

    @Override
    public void delete(UUID commentId) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User not Found"));

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundKeyException(commentId));

        if (existingComment.getUser().getUserId().equals(authUser.getUserId())) {
            existingComment.setAvailability(ActivityStatus.NOT_AVAILABLE);
            commentRepository.save(existingComment);
        }
    }
}
