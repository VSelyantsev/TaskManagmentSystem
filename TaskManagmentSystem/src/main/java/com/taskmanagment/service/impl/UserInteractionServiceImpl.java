package com.taskmanagment.service.impl;

import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.dto.response.UserTaskResponse;
import com.taskmanagment.exception.NotFoundException;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.repository.TaskRepository;
import com.taskmanagment.service.UserInteractionService;
import com.taskmanagment.service.UserService;
import com.taskmanagment.utils.EnumUtils;
import com.taskmanagment.utils.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInteractionServiceImpl implements UserInteractionService {

    private final UserService userService;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public UserTaskResponse appointExecutor(UUID userId, UUID taskId) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundKeyException(taskId));

        UserTaskResponse existingResponse = UserTaskResponse.builder()
                .responses(new ArrayList<>())
                .build();

        boolean isAvailable = userService.findUserById(userId).getAvailability().equals(ActivityStatus.AVAILABLE);
        if (authUser.getUserId().equals(existingTask.getAuthorId()) & isAvailable) {
            existingTask.setExecutorId(userId);
            taskRepository.save(existingTask);
            existingResponse.setUserId(userId);
            existingResponse.getResponses().add(taskMapper.toResponse(existingTask));
        }
        return existingResponse;
    }

    @Override
    public TaskResponse changeTaskStatus(UUID taskId, String executionStatus) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundKeyException(taskId));

        boolean isAvailable = authUser.getAvailability().equals(ActivityStatus.AVAILABLE);
        boolean isExecutor = authUser.getUserId().equals(existingTask.getExecutorId());

        if (isAvailable & isExecutor) {
            existingTask.setStatus(EnumUtils.extractStatusFromString(executionStatus));
            taskRepository.save(existingTask);
        }
        return taskMapper.toResponse(existingTask);
    }

    @Override
    public Page<TaskResponse> findAllPage(int offset, int pageSize, UUID userId) {
        Pageable pageable = PageRequest.of(offset, pageSize);
        return taskRepository.findAllByExecutorId(userId, pageable)
                .map(taskMapper::toResponseWithComments);
    }
}
