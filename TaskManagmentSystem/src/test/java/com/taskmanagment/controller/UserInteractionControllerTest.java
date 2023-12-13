package com.taskmanagment.controller;

import com.taskmanagment.configuration.ContainerConfiguration;
import com.taskmanagment.dto.jwt.JwtRequest;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.repository.TaskRepository;
import com.taskmanagment.repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@Sql(scripts = {"classpath:/sql/user-interaction.sql"})
@SpringBootTest
@AutoConfigureMockMvc
public class UserInteractionControllerTest {

    @Container
    PostgreSQLContainer<?> container = ContainerConfiguration.getInstance();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MockMvc mockMvc;

    private String token;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REQUEST_MAPPING = "/api/user/task";
    private static final UUID EXECUTOR_ID = UUID.fromString("32e22dc9-5acd-4dd8-b129-2b699ea8c35a");
    private static final UUID TASK_ID = UUID.fromString("45b5a1fa-5680-402c-a928-05818f165146");
    private static final UUID NON_EXIST_TASK_ID = UUID.fromString("45b5a1fa-5680-402c-a928-05818f165147");
    private static final UUID USER_EXECUTOR_ID = UUID.fromString("b808e96b-dec4-4f7a-9c2e-9222abc4db70");
    private static final UUID TASK_ID_WITH_APPOINT_EXECUTOR_ID = UUID.fromString("a6471dab-5dd0-44fd-99c0-2d43aaa73817");
    private static final Integer OFFSET = 0;
    private static final Integer PAGE_SIZE = 3;

    private static final JwtRequest JWT_REQUEST = new JwtRequest("test3@mail.ru", "12345678");

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
    void appointExecutor_shouldReturnResponse200() throws Exception {
        mockMvc.perform(get(REQUEST_MAPPING + "?userId=" + EXECUTOR_ID + "&taskId=" + TASK_ID)
                        .header("Authorization", this.token))
                .andExpect(status().isOk());

        Task taskToExecution = taskRepository.findById(TASK_ID)
                .orElseThrow(() -> new NotFoundKeyException(TASK_ID));

        User executor = userRepository.findById(EXECUTOR_ID)
                .orElseThrow(() -> new NotFoundKeyException(EXECUTOR_ID));

        assertNotNull(taskToExecution);
        assertNotNull(executor);

        assertEquals(taskToExecution.getExecutorId(), executor.getUserId());
    }

    @Test
    void appointExecutor_withUnAuthorized_shouldReturnStatus404andThrowNotFoundException() throws Exception {
        MvcResult result =  mockMvc.perform(
                get(REQUEST_MAPPING + "?userId=" + EXECUTOR_ID + "&taskId=" + NON_EXIST_TASK_ID)
                .header("Authorization", this.token))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }

    @Test
    void changeTaskStatus_shouldReturnStatus200() throws Exception {
        mockMvc.perform(patch(REQUEST_MAPPING + "/" + TASK_ID_WITH_APPOINT_EXECUTOR_ID)
                        .header("Authorization", this.token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ExecutionStatus.COMPLETED.getName())))
                .andExpect(status().isOk());

        Task taskWithChangedStatus = taskRepository.findById(TASK_ID_WITH_APPOINT_EXECUTOR_ID)
                .orElseThrow(() -> new NotFoundKeyException(TASK_ID_WITH_APPOINT_EXECUTOR_ID));
        assertNotNull(taskWithChangedStatus);

        assertEquals(taskWithChangedStatus.getStatus(), ExecutionStatus.COMPLETED);
    }

    @Test
    void changeTaskStatus_withInvalidTaskId_shouldReturnStatus404andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(
                patch(REQUEST_MAPPING + "/" + NON_EXIST_TASK_ID)
                        .header("Authorization", this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ExecutionStatus.COMPLETED.getName())))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }

    @Test
    void findAllPage_shouldReturnStatus200() throws Exception {
        mockMvc.perform(
                get(REQUEST_MAPPING + "/" +USER_EXECUTOR_ID + "?offset=" + OFFSET + "&pageSize=" + PAGE_SIZE)
                        .header("Authorization", this.token))
                .andExpect(status().isOk());
        Pageable pageable = PageRequest.of(OFFSET, PAGE_SIZE);
        Page<Task> page = taskRepository.findAllByExecutorId(USER_EXECUTOR_ID, pageable);
        assertNotNull(page);
    }
}
