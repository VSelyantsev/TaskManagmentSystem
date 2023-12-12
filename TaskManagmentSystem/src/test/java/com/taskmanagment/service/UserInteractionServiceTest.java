package com.taskmanagment.service;

import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.dto.response.UserTaskResponse;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.model.enums.Priority;
import com.taskmanagment.repository.TaskRepository;
import com.taskmanagment.service.impl.UserInteractionServiceImpl;
import com.taskmanagment.service.impl.UserServiceImpl;
import com.taskmanagment.utils.EnumUtils;
import com.taskmanagment.utils.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInteractionServiceTest {

    @InjectMocks
    private UserInteractionServiceImpl userService;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    private static final UUID VALID_UUID = UUID.fromString("67b24186-cfeb-4d41-99ff-2ce2fe7c975c");

    private static final UUID VALID_TASK_UUID = UUID.fromString("cdb92f9d-911a-4d18-ac6f-56d3fe836aa2");


    private static final UserResponse AUTH_RESPONSE = UserResponse.builder()
            .userId(VALID_UUID)
            .availability(ActivityStatus.AVAILABLE)
            .build();

    private static final Task VALID_TASK = Task.builder()
            .taskId(VALID_TASK_UUID)
            .availability(ActivityStatus.AVAILABLE)
            .user(User.builder().userId(VALID_UUID).build())
            .taskPriority(Priority.LOW)
            .authorId(VALID_UUID)
            .comments(new ArrayList<>())
            .status(ExecutionStatus.IN_PROCESS)
            .build();

    @Test
    void appointExecutor_shouldReturnUserTaskResponse() throws Exception {
        when(userServiceImpl.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.of(VALID_TASK));
        when(userServiceImpl.findUserById(VALID_UUID)).thenReturn(
                UserResponse.builder()
                        .userId(VALID_UUID)
                        .availability(ActivityStatus.AVAILABLE)
                .build()
        );

        when(taskRepository.save(VALID_TASK)).thenReturn(VALID_TASK);
        when(taskMapper.toResponse(VALID_TASK)).thenReturn(TaskResponse.builder()
                        .taskId(VALID_TASK.getTaskId())
                .build());

        UserTaskResponse actualResponse = userService.appointExecutor(VALID_UUID, VALID_TASK_UUID);
        assertNotNull(actualResponse);

        verify(userServiceImpl, times(1)).getCurrentAuthenticationUser();
        verify(userServiceImpl, times(1)).findUserById(VALID_UUID);
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
        verify(taskRepository, times(1)).save(VALID_TASK);
        verify(taskMapper, times(1)).toResponse(VALID_TASK);
    }

    @Test
    void appointExecutor_withInvalidTaskId_shouldThrowNotFoundKeyException() throws Exception {
        when(userServiceImpl.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundKeyException.class, () -> userService.appointExecutor(VALID_UUID, VALID_TASK_UUID));

        verify(userServiceImpl, times(1)).getCurrentAuthenticationUser();
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
    }

    @Test
    void changeTaskStatus_shouldReturnTaskResponse() throws Exception {
        try (MockedStatic<EnumUtils> utils = Mockito.mockStatic(EnumUtils.class)) {
            utils.when(() -> EnumUtils.extractStatusFromString(anyString())).thenReturn(ExecutionStatus.COMPLETED);
        }

        when(userServiceImpl.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.of(VALID_TASK));
        VALID_TASK.setExecutorId(VALID_UUID);

        when(taskRepository.save(VALID_TASK)).thenReturn(VALID_TASK);
        when(taskMapper.toResponse(VALID_TASK)).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return TaskResponse.builder()
                    .taskId(task.getTaskId())
                    .status(ExecutionStatus.COMPLETED)
                    .build();
        });

        TaskResponse actualResponse = userService.changeTaskStatus(VALID_TASK_UUID, ExecutionStatus.COMPLETED.getName());
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getStatus(), ExecutionStatus.COMPLETED);
    }

    @Test
    void findAllPage_shouldReturnPage() throws Exception {
        int offset = 0;
        int pageSize = 3;

        Pageable pageable = PageRequest.of(offset, pageSize);
        List<Task> taskList = List.of(VALID_TASK);
        Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
        when(taskRepository.findAllByExecutorId(VALID_UUID, pageable)).thenReturn(taskPage);

        TaskResponse response = TaskResponse.builder()
                .taskId(VALID_TASK.getTaskId())
                .status(ExecutionStatus.IN_PROCESS)
                .availability(ActivityStatus.AVAILABLE)
                .build();

        List<TaskResponse> responses = List.of(response);

        Page<TaskResponse> acturalPage = userService.findAllPage(offset, pageSize, VALID_UUID);

        assertEquals(taskPage.getTotalElements(), acturalPage.getTotalElements());
        assertEquals(responses.size(), acturalPage.getContent().size());
        verify(taskRepository, times(1)).findAllByExecutorId(VALID_UUID, pageable);
        verify(taskMapper, times(taskList.size())).toResponseWithComments(any(Task.class));
    }
}
