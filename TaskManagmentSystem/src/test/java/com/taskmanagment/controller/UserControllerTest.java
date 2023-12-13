package com.taskmanagment.controller;

import com.taskmanagment.configuration.ContainerConfiguration;
import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.exception.NotFoundLoginNameException;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.repository.UserRepository;
import com.taskmanagment.utils.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:/sql/user.sql"})
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Container
    PostgreSQLContainer<?> container = ContainerConfiguration.getInstance();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final String REQUEST_MAPPING = "/api/users";
    private static final UUID VALID_UUID = UUID.fromString("34783ace-f846-475e-b6d7-5e27c2ce8a3e");
    private static final UUID NON_EXIST_UUID = UUID.fromString("5871fab0-3610-48ba-ad28-e24fb490baee");

    private static final UserRequest VALID_REQUEST = UserRequest.builder()
            .login("selvlad775@mail.ru")
            .password("123456")
            .build();

    private static final UserRequest REQUEST_TO_UPDATE = UserRequest.builder()
            .login("test@mail.ru")
            .password("654321")
            .build();

    private static final User VALID_USER = User.builder()
            .userId(VALID_UUID)
            .login("test2@mail.ru")
            .availability(ActivityStatus.AVAILABLE)
            .build();

    @Test
    void create_shouldReturnResponseStatus201() throws Exception {
        mockMvc.perform(
                post(REQUEST_MAPPING + "/" + "registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST))
        ).andExpect(status().isCreated());

        User user = userRepository.findByLogin(VALID_REQUEST.getLogin())
                .orElseThrow(() -> new NotFoundLoginNameException(VALID_REQUEST.getLogin()));

        assertNotNull(user);
        assertEquals(user.getLogin(), VALID_REQUEST.getLogin());
        assertTrue(encoder.matches(VALID_REQUEST.getPassword(), user.getHashPassword()));
        assertEquals(user.getAvailability(), ActivityStatus.AVAILABLE);
        assertNotNull(user.getTasks());
        assertNotNull(user.getComments());
        assertNotNull(user.getRoles());
    }

    @Test
    @WithMockUser(username = "user")
    void findById_withValidId_shouldReturnResponseStatus200() throws Exception {
        mockMvc.perform(get(REQUEST_MAPPING + "/" + VALID_UUID))
                .andExpect(status().isOk());

        User user = userRepository.findById(VALID_UUID)
                .orElseThrow(() -> new NotFoundKeyException(VALID_UUID));

        assertNotNull(user);
        assertEquals(user.getUserId(), VALID_USER.getUserId());
        assertEquals(user.getLogin(), VALID_USER.getLogin());
        assertEquals(user.getAvailability(), VALID_USER.getAvailability());
    }

    @Test
    @WithMockUser(username = "user")
    void findById_withInvalidId_shouldReturnStatus404_andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(
                get(REQUEST_MAPPING + "/" + NON_EXIST_UUID))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }

    @Test
    @WithMockUser(username = "user")
    void findAll_shouldReturnResponseStatus200() throws Exception {
        List<UserResponse> actualResponses = userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        mockMvc.perform(get(REQUEST_MAPPING))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(actualResponses.size())))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user")
    void update_shouldReturnResponseStatus200() throws Exception {
        mockMvc.perform(put(REQUEST_MAPPING + "/" + VALID_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(REQUEST_TO_UPDATE))
        ).andExpect(status().isOk());

        User actualUser = userRepository.findById(VALID_UUID)
                .orElseThrow(() -> new NotFoundKeyException(VALID_UUID));

        assertNotNull(actualUser);
        assertEquals(actualUser.getLogin(), REQUEST_TO_UPDATE.getLogin());
        assertTrue(encoder.matches(REQUEST_TO_UPDATE.getPassword(), actualUser.getHashPassword()));
    }

    @Test
    @WithMockUser(username = "user")
    void update_withInvalidId_shouldReturnStatus404_andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(
                put(REQUEST_MAPPING + "/" + NON_EXIST_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST_TO_UPDATE)))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }

    @Test
    @WithMockUser(username = "user")
    void delete_shouldReturnStatus204() throws Exception {
        mockMvc.perform(delete(REQUEST_MAPPING + "/" + VALID_UUID))
                .andExpect(status().isNoContent());

        User unvailableUser = userRepository.findById(VALID_UUID)
                .orElseThrow(() -> new NotFoundKeyException(VALID_UUID));

        assertNotNull(unvailableUser);
        assertEquals(unvailableUser.getAvailability(), ActivityStatus.NOT_AVAILABLE);
    }

    @Test
    @WithMockUser
    void delete_withInvalidId_shouldReturnResponseStatus204_andThrowNotFoundKeyException() throws Exception {
        MvcResult result = mockMvc.perform(delete(REQUEST_MAPPING + "/" + NON_EXIST_UUID))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof NotFoundKeyException);
    }
}
