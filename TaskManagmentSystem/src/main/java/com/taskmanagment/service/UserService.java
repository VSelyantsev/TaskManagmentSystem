package com.taskmanagment.service;

import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserRequest userRequest);
    UserResponse findUserById(UUID userId);
    List<UserResponse> findAll();
    UserResponse update(UUID userId, UserRequest userRequest);
    void delete(UUID userId);
    Optional<UserResponse> getCurrentAuthenticationUser();
}
