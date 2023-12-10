package com.taskmanagment.service;

import com.taskmanagment.dto.request.CommentRequest;
import com.taskmanagment.dto.response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse create(UUID taskId, CommentRequest commentRequest);
    CommentResponse findCommentById(UUID commentId);
    List<CommentResponse> findAll();
    CommentResponse update(UUID commentId, CommentRequest commentRequest);
    void delete(UUID commentId);
}
