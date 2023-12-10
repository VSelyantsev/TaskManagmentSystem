package com.taskmanagment.service;

import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.dto.response.TaskResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse create(TaskRequest taskRequest);
    TaskResponse findTaskById(UUID taskId);
    List<TaskResponse> findAll();
    TaskResponse update(UUID taskId, TaskRequest taskRequest);
    TaskResponse updateTaskStatus(UUID taskId, String executionStatus);
    void delete(UUID taskId);
}
