package com.taskmanagment.service;

import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserTaskResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserInteractionService {
    UserTaskResponse appointExecutor(UUID userId, UUID taskId);
    TaskResponse changeTaskStatus(UUID taskId, String executionStatus);
    Page<TaskResponse> findAllPage(int offset, int pageSize, UUID userId);
}
