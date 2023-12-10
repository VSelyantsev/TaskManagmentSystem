package com.taskmanagment.controller;

import com.taskmanagment.api.UserInteractionApi;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserTaskResponse;
import com.taskmanagment.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserInteractionController implements UserInteractionApi {

    private final UserInteractionService userInteractionService;

    @Override
    public UserTaskResponse appointExecutor(UUID userId, UUID taskId) {
        return userInteractionService.appointExecutor(userId, taskId);
    }

    @Override
    public TaskResponse changeTaskStatus(UUID taskId, String executionStatus) {
        return userInteractionService.changeTaskStatus(taskId, executionStatus);
    }

    @Override
    public Page<TaskResponse> findAllPage(int offset, int pageSize, UUID userId) {
        return userInteractionService.findAllPage(offset, pageSize, userId);
    }
}
