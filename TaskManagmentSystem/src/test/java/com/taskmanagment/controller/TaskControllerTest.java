package com.taskmanagment.controller;

import com.taskmanagment.configuration.ContainerConfiguration;
import com.taskmanagment.dto.jwt.JwtRequest;
import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.exception.NotFoundLoginNameException;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.model.enums.Priority;
import com.taskmanagment.repository.TaskRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:/sql/task.sql"})
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Container
    PostgreSQLContainer<?> container = ContainerConfiguration.getInstance();

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MockMvc mockMvc;

    String token;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REQUEST_MAPPING = "/api/tasks";
    private static final String AUTHORIZATION = "Authorization";
    private static final UUID EXIST_TASK_ID = UUID.fromString("545e8f38-ef6e-4121-a8b3-3e2715a048b0");
    private static final UUID EXECUTOR_ID = UUID.fromString("b808e96b-dec4-4f7a-9c2e-9222abc4db70");
    private static final UUID NON_EXIST_TASK_ID = UUID.fromString("545e8f38-ef6e-4121-a8b3-3e2715a048b1");
    private static final JwtRequest JWT_REQUEST = new JwtRequest("test3@mail.ru", "12345678");

    private static final TaskRequest VALID_REQUEST = TaskRequest.builder()
            .title("test with valid request")
            .description("something for fuel this descriptions")
            .executorId(EXECUTOR_ID)
            .taskPriority(Priority.LOW)
            .build();

    private static final TaskRequest REQUEST_TO_UPDATE = TaskRequest.builder()
            .title("updated test title")
            .description("updated test description")
            .build();

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(JWT_REQUEST)));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);
        this.token = "Bearer " + jsonObject.getString("accessToken");
    }

    @Test
    void create_shouldReturnStatus201() throws Exception {
        mockMvc.perform(post(REQUEST_MAPPING)
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST)))
                .andExpect(status().isCreated());

        Task createdTask = taskRepository.findByTitle(VALID_REQUEST.getTitle())
                .orElseThrow(() -> new NotFoundLoginNameException(VALID_REQUEST.getTitle()));

        assertNotNull(createdTask);
        assertNotNull(createdTask.getUser());
        assertNotNull(createdTask.getComments());
        assertNotNull(createdTask.getAuthorId());
        assertEquals(createdTask.getTitle(), VALID_REQUEST.getTitle());
        assertEquals(createdTask.getDescription(), VALID_REQUEST.getDescription());
        assertEquals(createdTask.getTaskPriority(), VALID_REQUEST.getTaskPriority());
        assertEquals(createdTask.getStatus(), ExecutionStatus.IN_PROCESS);
        assertEquals(createdTask.getAvailability(), ActivityStatus.AVAILABLE);
    }

    @Test
    void findById_shouldReturnStatus200() throws Exception {
        mockMvc.perform(get(REQUEST_MAPPING + "/" + EXIST_TASK_ID)
                .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.taskId").value(EXIST_TASK_ID.toString()))
                .andExpect(jsonPath("$.authorId").isNotEmpty())
                .andExpect(jsonPath("$.executorId").value(EXECUTOR_ID.toString()))
                .andExpect(jsonPath("$.availability").isNotEmpty())
                .andExpect(jsonPath("$.status").value(ExecutionStatus.IN_PROCESS.getName()))
                .andExpect(jsonPath("$.taskPriority").value(Priority.LOW.getName()))
                .andExpect(status().isOk());

        Task existTask = taskRepository.findById(EXIST_TASK_ID)
                .orElseThrow(() -> new NotFoundKeyException(EXIST_TASK_ID));

        assertNotNull(existTask);
    }

    @Test
    void findById_withInvalidId_shouldReturnStatus404andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(get(REQUEST_MAPPING + "/" + NON_EXIST_TASK_ID)
                .header(AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }

    @Test
    void findAll_shouldReturnStatus200() throws Exception {
        List<Task> actualList = taskRepository.findAll()
                .stream()
                .filter(task -> task.getAvailability().equals(ActivityStatus.AVAILABLE))
                .filter(task -> task.getExecutorId().equals(EXECUTOR_ID))
                .toList();

        mockMvc.perform(get(REQUEST_MAPPING)
                .header(AUTHORIZATION, this.token))
                .andExpect(jsonPath("$", hasSize(actualList.size())))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturnStatus200() throws Exception {
        mockMvc.perform(put(REQUEST_MAPPING + "/" + EXIST_TASK_ID)
                .header(AUTHORIZATION, this.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(REQUEST_TO_UPDATE)))
                .andExpect(status().isOk());

        Task updateTask = taskRepository.findById(EXIST_TASK_ID)
                .orElseThrow(() -> new NotFoundKeyException(EXIST_TASK_ID));

        assertNotNull(updateTask);
        assertEquals(updateTask.getTitle(), REQUEST_TO_UPDATE.getTitle());
        assertEquals(updateTask.getDescription(), REQUEST_TO_UPDATE.getDescription());
    }

    @Test
    void update_withInvalidTaskId_shouldReturnStatus404andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(put(REQUEST_MAPPING + "/" + NON_EXIST_TASK_ID)
                .header(AUTHORIZATION, this.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(REQUEST_TO_UPDATE)))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }

    @Test
    void updateTaskStatus_shouldReturnStatus200() throws Exception {
        mockMvc.perform(patch(REQUEST_MAPPING + "/" + EXIST_TASK_ID)
                .header(AUTHORIZATION, this.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ExecutionStatus.COMPLETED.getName())))
                .andExpect(status().isOk());

        Task taskWithUpdatedStatus = taskRepository.findById(EXIST_TASK_ID)
                .orElseThrow(() -> new NotFoundKeyException(EXIST_TASK_ID));

        assertNotNull(taskWithUpdatedStatus);
        assertEquals(taskWithUpdatedStatus.getStatus(), ExecutionStatus.COMPLETED);
    }

    @Test
    void updateTaskStatus_withInvalidTaskId_shouldReturnStatus404andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(patch(REQUEST_MAPPING + "/" + NON_EXIST_TASK_ID)
                        .header(AUTHORIZATION, this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ExecutionStatus.COMPLETED.getName())))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }

    @Test
    void delete_shouldReturnStatus204() throws Exception {
        mockMvc.perform(delete(REQUEST_MAPPING + "/" + EXIST_TASK_ID)
                .header(AUTHORIZATION, this.token))
                .andExpect(status().isNoContent());

        Task unvailableTask = taskRepository.findById(EXIST_TASK_ID)
                .orElseThrow(() -> new NotFoundKeyException(EXIST_TASK_ID));

        assertNotNull(unvailableTask);
        assertEquals(unvailableTask.getAvailability(), ActivityStatus.NOT_AVAILABLE);
    }

    @Test
    void delete_withInvalidTaskId_shouldReturnStatus404andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(delete(REQUEST_MAPPING + "/" + NON_EXIST_TASK_ID)
                .header(AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }
}
