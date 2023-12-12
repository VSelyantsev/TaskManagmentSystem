package com.taskmanagment.service;

import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.exception.NotFoundException;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.Role;
import com.taskmanagment.repository.UserRepository;
import com.taskmanagment.service.impl.UserServiceImpl;
import com.taskmanagment.utils.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final UUID VALID_UUID = UUID.fromString("67b24186-cfeb-4d41-99ff-2ce2fe7c975c");
    private static final UUID VALID_UUID_2 = UUID.fromString("8c7a6b07-cbbe-437d-bd7e-b7b0fc6acf68");

    private static final UserRequest VALID_REQUEST = UserRequest.builder()
            .login("test@mail.ru")
            .password("123456")
            .build();

    private static final UserRequest VALID_REQUEST_2 = UserRequest.builder()
            .login("testTwo@mail.ru")
            .password("654321")
            .build();

    private static final User VALID_ENTITY = User.builder()
            .userId(VALID_UUID)
            .login(VALID_REQUEST.getLogin())
            .hashPassword(encoder.encode(VALID_REQUEST.getPassword()))
            .availability(ActivityStatus.AVAILABLE)
            .tasks(new ArrayList<>())
            .comments(new ArrayList<>())
            .roles(Set.of(Role.USER))
            .build();

    private static final User VALID_ENTITY_2 = User.builder()
            .userId(VALID_UUID_2)
            .login(VALID_REQUEST_2.getLogin())
            .hashPassword(encoder.encode(VALID_REQUEST_2.getPassword()))
            .availability(ActivityStatus.AVAILABLE)
            .tasks(new ArrayList<>())
            .comments(new ArrayList<>())
            .roles(Set.of(Role.USER))
            .build();

    private static final UserResponse VALID_RESPONSE = UserResponse.builder()
            .userId(VALID_ENTITY.getUserId())
            .login(VALID_ENTITY.getLogin())
            .availability(VALID_ENTITY.getAvailability())
            .build();

    @Test
    void findAll_shouldReturnListOfUserResponses() throws Exception {
        User user1 = User.builder()
                .userId(VALID_UUID)
                .login("test@mail.ru")
                .availability(ActivityStatus.AVAILABLE)
                .build();

        User user2 = User.builder()
                .userId(VALID_UUID_2)
                .login("test2@mail.ru")
                .availability(ActivityStatus.AVAILABLE)
                .build();


        List<User> userList = List.of(user1, user2);

        UserResponse userResponse1 = UserResponse.builder()
                .userId(user1.getUserId())
                .login(user1.getLogin())
                .availability(user1.getAvailability())
                .build();

        UserResponse userResponse2 = UserResponse.builder()
                .userId(user2.getUserId())
                .login(user2.getLogin())
                .availability(user2.getAvailability())
                .build();

        List<UserResponse> expectedResponses = List.of(userResponse1, userResponse2);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                    .userId(user.getUserId())
                    .login(user.getLogin())
                    .availability(user.getAvailability())
                    .build();
        });

        List<UserResponse> actualResponse = userService.findAll();

        assertNotNull(actualResponse);
        assertEquals(expectedResponses.size(), actualResponse.size());

        List<UUID> expectedIds = expectedResponses.stream()
                .map(UserResponse::getUserId)
                .toList();

        List<UUID> actualIds = actualResponse.stream()
                .map(UserResponse::getUserId)
                .toList();

        assertTrue(expectedIds.containsAll(actualIds));

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(2)).toResponse(any(User.class));
    }

    @Test
    void create_shouldReturnUserResponse() throws Exception {
        User entity = User.builder()
                .userId(VALID_UUID)
                .login(VALID_REQUEST.getLogin())
                .hashPassword(encoder.encode(VALID_REQUEST.getPassword()))
                .build();

        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(entity);
        when(userRepository.save(any(User.class))).thenReturn(entity);
        when(userMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserResponse.builder()
                    .userId(user.getUserId())
                    .login(user.getLogin())
                    .build();
        });

        UserResponse actualResponse = userService.create(VALID_REQUEST);

        assertNotNull(actualResponse);
        assertTrue(encoder.matches(VALID_REQUEST.getPassword(), entity.getHashPassword()));
        assertNotNull(entity.getRoles());
        assertNotNull(entity.getComments());
        assertNotNull(entity.getTasks());

        verify(userMapper, times(1)).toEntity(VALID_REQUEST);
        verify(userRepository, times(1)).save(entity);
        verify(userMapper, times(1)).toResponse(entity);
    }

    @Test
    void findById_ShouldReturnUserResponse() throws Exception {
        UserResponse expectedResponse = UserResponse.builder()
                .userId(VALID_ENTITY.getUserId())
                .login(VALID_ENTITY.getLogin())
                .availability(VALID_ENTITY.getAvailability())
                .build();

        when(userRepository.findById(VALID_UUID)).thenReturn(Optional.of(VALID_ENTITY));
        when(userMapper.toResponse(VALID_ENTITY)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.findUserById(VALID_UUID);
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getUserId(), expectedResponse.getUserId());
        assertEquals(actualResponse.getLogin(), expectedResponse.getLogin());
        assertEquals(actualResponse.getAvailability(), expectedResponse.getAvailability());

        verify(userRepository, times(1)).findById(VALID_UUID);
        verify(userMapper, times(1)).toResponse(VALID_ENTITY);
    }

    @Test
    void findById_WithInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userRepository.findById(VALID_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundKeyException.class, () -> userService.findUserById(VALID_UUID));

        verify(userRepository, times(1)).findById(VALID_UUID);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void update_shouldReturnUserResponse() throws Exception {
        when(userRepository.findById(VALID_UUID)).thenReturn(Optional.of(VALID_ENTITY));
        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(VALID_ENTITY_2);
        when(userRepository.save(any(User.class))).thenReturn(VALID_ENTITY);
        when(userMapper.toResponse(VALID_ENTITY)).thenReturn(VALID_RESPONSE);

        UserResponse actualResponse = userService.update(VALID_UUID, VALID_REQUEST);

        assertNotNull(actualResponse);
        assertEquals(actualResponse.getUserId(), VALID_RESPONSE.getUserId());
        assertEquals(actualResponse.getLogin(), VALID_RESPONSE.getLogin());
        assertEquals(actualResponse.getAvailability(), VALID_RESPONSE.getAvailability());

        verify(userRepository, times(1)).findById(VALID_UUID);
        verify(userMapper, times(1)).toEntity(any(UserRequest.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    void update_WithInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userRepository.findById(VALID_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(VALID_UUID, VALID_REQUEST));

        verify(userRepository, times(1)).findById(VALID_UUID);
    }

    @Test
    void delete_void_checkIfAvailabilityChanged() throws Exception {
        when(userRepository.findById(VALID_UUID)).thenReturn(Optional.of(VALID_ENTITY));

        User unvailableUser = User.builder()
                .userId(VALID_ENTITY.getUserId())
                .login(VALID_ENTITY.getLogin())
                .availability(ActivityStatus.NOT_AVAILABLE)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(unvailableUser);
        userService.delete(VALID_UUID);

        assertEquals(unvailableUser.getAvailability(), ActivityStatus.NOT_AVAILABLE);

        verify(userRepository, times(1)).findById(VALID_UUID);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void delete_WithInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userRepository.findById(VALID_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(VALID_UUID));

        verify(userRepository, times(1)).findById(VALID_UUID);
    }

}
