package com.taskmanagment.controller;

import com.taskmanagment.api.CommentApi;
import com.taskmanagment.dto.request.CommentRequest;
import com.taskmanagment.dto.response.CommentResponse;
import com.taskmanagment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    public CommentResponse create(UUID taskId, CommentRequest commentRequest) {
        return commentService.create(taskId, commentRequest);
    }

    @Override
    public CommentResponse findCommentById(UUID commentId) {
        return commentService.findCommentById(commentId);
    }

    @Override
    public List<CommentResponse> findAll() {
        return commentService.findAll();
    }

    @Override
    public CommentResponse update(UUID commentId, CommentRequest commentRequest) {
        return commentService.update(commentId, commentRequest);
    }

    @Override
    public void delete(UUID commentId) {
        commentService.delete(commentId);
    }
}
