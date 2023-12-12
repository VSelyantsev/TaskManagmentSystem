package com.taskmanagment.service;

import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.exception.NotFoundException;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.model.enums.Priority;
import com.taskmanagment.repository.TaskRepository;
import com.taskmanagment.service.impl.TaskServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    private static final UUID VALID_UUID = UUID.fromString("67b24186-cfeb-4d41-99ff-2ce2fe7c975c");

    private static final UUID VALID_TASK_UUID = UUID.fromString("cdb92f9d-911a-4d18-ac6f-56d3fe836aa2");
    private static final UUID VALID_TASK_UUID_2 = UUID.fromString("cdb92f9d-911a-4d18-ac6f-56d3fe836aa3");

    private static final UserResponse AUTH_RESPONSE = UserResponse.builder()
            .userId(VALID_UUID)
            .availability(ActivityStatus.AVAILABLE)
            .build();

    private static final TaskRequest VALID_TASK_REQUEST = TaskRequest.builder()
            .taskId(VALID_TASK_UUID)
            .title("Test")
            .description("descriptions")
            .taskPriority(Priority.LOW)
            .build();

    private static final Task VALID_TASK = Task.builder()
            .taskId(VALID_TASK_UUID)
            .availability(ActivityStatus.AVAILABLE)
            .user(User.builder().userId(VALID_UUID).build())
            .title("test")
            .description("description")
            .taskPriority(Priority.LOW)
            .authorId(VALID_UUID)
            .executorId(VALID_UUID)
            .comments(new ArrayList<>())
            .status(ExecutionStatus.IN_PROCESS)
            .build();

    @Test
    void create_shouldReturnTaskResponse() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));

        Task entity = Task.builder()
                .taskId(VALID_TASK_REQUEST.getTaskId())
                .user(User.builder().userId(VALID_UUID).build())
                .build();


        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(entity);
        when(taskRepository.save(any(Task.class))).thenReturn(entity);
        when(taskMapper.toResponse(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return TaskResponse.builder()
                    .taskId(task.getTaskId())
                    .authorId(task.getAuthorId())
                    .build();
        });

        TaskResponse actualResponse = taskService.create(VALID_TASK_REQUEST);
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getAuthorId(), entity.getAuthorId());

        verify(taskMapper, times(1)).toEntity(VALID_TASK_REQUEST);
        verify(taskRepository, times(1)).save(entity);
        verify(taskMapper, times(1)).toResponse(entity);
    }

    @Test
    void findById_shouldReturnTaskResponse() throws Exception {
        TaskResponse expectedResponse = TaskResponse.builder()
                .taskId(VALID_TASK.getTaskId())
                .availability(VALID_TASK.getAvailability())
                .taskPriority(VALID_TASK.getTaskPriority())
                .title(VALID_TASK.getTitle())
                .description(VALID_TASK.getDescription())
                .build();

        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.of(VALID_TASK));
        when(taskMapper.toResponseWithComments(VALID_TASK)).thenReturn(expectedResponse);

        TaskResponse actualResponse = taskService.findTaskById(VALID_TASK_UUID);

        assertNotNull(actualResponse);
        assertEquals(actualResponse.getTaskId(), expectedResponse.getTaskId());
        assertEquals(actualResponse.getAvailability(), expectedResponse.getAvailability());
        assertEquals(actualResponse.getTaskPriority(), expectedResponse.getTaskPriority());
        assertEquals(actualResponse.getTitle(), expectedResponse.getTitle());
        assertEquals(actualResponse.getDescription(), expectedResponse.getDescription());

        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
        verify(taskMapper, times(1)).toResponseWithComments(VALID_TASK);
    }

    @Test
    void findById_withInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundKeyException.class, () -> taskService.findTaskById(VALID_TASK_UUID));

        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
    }

    @Test
    void findAll_shouldReturnListOfTaskResponses() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));

        Task task1 = Task.builder()
                .taskId(VALID_TASK_UUID)
                .executorId(AUTH_RESPONSE.getUserId())
                .availability(ActivityStatus.AVAILABLE)
                .build();

        Task task2 = Task.builder()
                .taskId(VALID_TASK_UUID_2)
                .executorId(AUTH_RESPONSE.getUserId())
                .availability(ActivityStatus.AVAILABLE)
                .build();

        List<Task> taskList = List.of(task1, task2);

        TaskResponse taskResponse1 = TaskResponse.builder()
                .taskId(task1.getTaskId())
                .executorId(AUTH_RESPONSE.getUserId())
                .availability(task1.getAvailability())
                .build();

        TaskResponse taskResponse2 = TaskResponse.builder()
                .taskId(task2.getTaskId())
                .executorId(AUTH_RESPONSE.getUserId())
                .availability(task2.getAvailability())
                .build();

        List<TaskResponse> expectedResponses = List.of(taskResponse1, taskResponse2);

        when(taskRepository.findAll()).thenReturn(taskList);

        when(taskMapper.toResponse(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return TaskResponse.builder()
                    .taskId(task.getTaskId())
                    .executorId(task.getExecutorId())
                    .availability(task.getAvailability())
                    .build();
        });

        List<TaskResponse> actualResponse = taskService.findAll();

        assertNotNull(actualResponse);
        assertEquals(expectedResponses.size(), actualResponse.size());

        verify(taskRepository, times(1)).findAll();
        verify(taskMapper, times(2)).toResponse(any(Task.class));
    }

    @Test
    void update_shouldReturnTaskResponse() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.of(VALID_TASK));

        when(taskRepository.existsByTaskIdAndAuthorId(any(), any())).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(VALID_TASK);
        when(taskMapper.toResponse(VALID_TASK)).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return TaskResponse.builder()
                    .taskId(task.getTaskId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .build();
        });



        TaskResponse actualResponse = taskService.update(VALID_TASK_UUID, VALID_TASK_REQUEST);
        assertNotNull(actualResponse);

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
        verify(taskRepository, times(1)).save(VALID_TASK);
        verify(taskMapper, times(1)).toResponse(VALID_TASK);
    }

    @Test
    void update_withInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundKeyException.class, () -> taskService.update(VALID_TASK_UUID, VALID_TASK_REQUEST));

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
    }

    @Test
    void updateTaskStatus_shouldReturnUpdatedTaskResponse() throws Exception {
        try (MockedStatic<EnumUtils> utils = Mockito.mockStatic(EnumUtils.class)) {
            utils.when(() -> EnumUtils.extractStatusFromString(anyString())).thenReturn(ExecutionStatus.COMPLETED);
        }

        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.of(VALID_TASK));
        when(taskRepository.existsByTaskIdAndAuthorId(any(), any())).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(VALID_TASK);
        when(taskMapper.toResponse(VALID_TASK)).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return TaskResponse.builder()
                    .taskId(task.getTaskId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .status(ExecutionStatus.COMPLETED)
                    .build();
        });

        TaskResponse actualResponse = taskService.updateTaskStatus(VALID_TASK_UUID, ExecutionStatus.COMPLETED.getName());
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getStatus(), ExecutionStatus.COMPLETED);

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
        verify(taskRepository, times(1)).existsByTaskIdAndAuthorId(any(), any());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).toResponse(VALID_TASK);
    }

    @Test
    void updateTaskStatus_withInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundKeyException.class,
                () -> taskService.updateTaskStatus(VALID_TASK_UUID, ExecutionStatus.COMPLETED.getName()));

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
    }

    @Test
    void delete_void_checkIfAvailabilityChanged() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.of(VALID_TASK));

        Task unvailableTask = Task.builder()
                .taskId(VALID_TASK_UUID)
                .availability(ActivityStatus.NOT_AVAILABLE)
                .build();

        when(taskRepository.existsByTaskIdAndAuthorId(any(), any())).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(unvailableTask);
        taskService.delete(VALID_TASK_UUID);

        assertEquals(unvailableTask.getAvailability(), ActivityStatus.NOT_AVAILABLE);

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
        verify(taskRepository, times(1)).existsByTaskIdAndAuthorId(any(), any());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void delete_withInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskRepository.findById(VALID_TASK_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.delete(VALID_TASK_UUID));

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(taskRepository, times(1)).findById(VALID_TASK_UUID);
    }

}
