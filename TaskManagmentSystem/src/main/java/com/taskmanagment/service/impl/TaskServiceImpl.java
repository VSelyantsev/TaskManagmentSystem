package com.taskmanagment.service.impl;

import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.exception.NotFoundException;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.repository.TaskRepository;
import com.taskmanagment.service.TaskService;
import com.taskmanagment.service.UserService;
import com.taskmanagment.utils.EnumUtils;
import com.taskmanagment.utils.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final UserService userService;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse create(TaskRequest taskRequest) {
        UserResponse response = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        Task existingTask = taskMapper.toEntity(taskRequest);
        existingTask.setUser(User.builder().userId(response.getUserId()).build());
        existingTask.setAvailability(ActivityStatus.AVAILABLE);
        existingTask.setAuthorId(response.getUserId());
        existingTask.setStatus(ExecutionStatus.IN_PROCESS);
        existingTask.setComments(new ArrayList<>());
        return taskMapper.toResponse(taskRepository.save(existingTask));
    }

    @Override
    public TaskResponse findTaskById(UUID taskId) {
        return taskMapper.toResponseWithComments(
                taskRepository.findById(taskId).orElseThrow(() -> new NotFoundKeyException(taskId))
        );
    }

    @Override
    public List<TaskResponse> findAll() {
        UserResponse existingUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        return taskRepository.findAll()
                .stream()
                .filter(entity -> entity.getAvailability().equals(ActivityStatus.AVAILABLE))
                .filter(entity -> entity.getExecutorId().equals(existingUser.getUserId()))
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    public TaskResponse update(UUID taskId, TaskRequest taskRequest) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundKeyException(taskId));

        if (inUserListTasks(existingTask.getTaskId(), authUser.getUserId())) {
            existingTask.setTitle(taskRequest.getTitle());
            existingTask.setDescription(taskRequest.getDescription());
            taskRepository.save(existingTask);
        }
        return taskMapper.toResponse(existingTask);
    }

    @Override
    public TaskResponse updateTaskStatus(UUID taskId, String  executionStatus) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundKeyException(taskId));

        if (inUserListTasks(existingTask.getTaskId(), authUser.getUserId())) {
            existingTask.setStatus(EnumUtils.extractStatusFromString(executionStatus));
            taskRepository.save(existingTask);
        }

        return taskMapper.toResponse(existingTask);
    }

    @Override
    public void delete(UUID taskId) {
        UserResponse authUser = userService.getCurrentAuthenticationUser()
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundKeyException(taskId));

        if (inUserListTasks(existingTask.getTaskId(), authUser.getUserId())) {
            existingTask.setAvailability(ActivityStatus.NOT_AVAILABLE);
            taskRepository.save(existingTask);
        }
    }

    private boolean inUserListTasks(UUID taskId, UUID authorId) {
        return taskRepository.existsByTaskIdAndAuthorId(taskId, authorId);
    }
}
