package com.taskmanagment.controller;

import com.taskmanagment.api.TaskApi;
import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @Override
    public TaskResponse create(TaskRequest taskRequest) {
        return taskService.create(taskRequest);
    }

    @Override
    public TaskResponse finTaskById(UUID taskId) {
        return taskService.findTaskById(taskId);
    }

    @Override
    public List<TaskResponse> findAll() {
        return taskService.findAll();
    }

    @Override
    public TaskResponse update(UUID taskId, TaskRequest taskRequest) {
        return taskService.update(taskId, taskRequest);
    }

    @Override
    public TaskResponse updateTaskStatus(UUID taskId, String  executionStatus) {
        return taskService.updateTaskStatus(taskId, executionStatus);
    }

    @Override
    public void delete(UUID taskId) {
        taskService.delete(taskId);
    }
}
