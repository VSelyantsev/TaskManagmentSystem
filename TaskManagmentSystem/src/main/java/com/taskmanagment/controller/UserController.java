package com.taskmanagment.controller;

import com.taskmanagment.api.UserApi;
import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public UserResponse create(UserRequest userRequest) {
        return userService.create(userRequest);
    }

    @Override
    public UserResponse findUserById(UUID userId) {
        return userService.findUserById(userId);
    }

    @Override
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @Override
    public UserResponse update(UUID userId, UserRequest userRequest) {
        return userService.update(userId, userRequest);
    }

    @Override
    public void delete(UUID userId) {
        userService.delete(userId);
    }
}
