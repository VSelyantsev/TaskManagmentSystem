package com.taskmanagment.service.impl;

import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.exception.AccessDeniedException;
import com.taskmanagment.exception.NotFoundKeyException;
import com.taskmanagment.exception.NotFoundLoginNameException;
import com.taskmanagment.model.enums.Role;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.repository.UserRepository;
import com.taskmanagment.service.UserService;
import com.taskmanagment.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse create(UserRequest userRequest) {
        User entity = userMapper.toEntity(userRequest);
        entity.setRoles(createRoles("USER"));
        entity.setAvailability(ActivityStatus.AVAILABLE);
        entity.setTasks(new ArrayList<>());
        entity.setComments(new ArrayList<>());
        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    public UserResponse findUserById(UUID userId) {
        return userMapper.toResponse(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundKeyException(userId))
        );
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .filter(entity -> entity.getAvailability().equals(ActivityStatus.AVAILABLE))
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse update(UUID userId, UserRequest userRequest) {
        User existingEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundKeyException(userId));
        if (existingEntity.getUserId().equals(userId)) {
            User requestWithHashedPassword = userMapper.toEntity(userRequest);
            existingEntity.setLogin(requestWithHashedPassword.getLogin());
            existingEntity.setHashPassword(requestWithHashedPassword.getHashPassword());
        }
        return userMapper.toResponse(userRepository.save(existingEntity));
    }

    @Override
    public void delete(UUID userId) {
        User entityToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundKeyException(userId));
        entityToDelete.setAvailability(ActivityStatus.NOT_AVAILABLE);
        userRepository.save(entityToDelete);
    }

    @Override
    public Optional<UserResponse> getCurrentAuthenticationUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException(ActivityStatus.ANONYMOUS);
        }

        String existingLoginName = auth.getName();
        User authUser = userRepository.findByLogin(existingLoginName)
                .orElseThrow(() -> new NotFoundLoginNameException(existingLoginName));

        if (!authUser.getAvailability().equals(ActivityStatus.AVAILABLE)) {
            throw new AccessDeniedException(ActivityStatus.NOT_AVAILABLE);
        }

        return Optional.of(userMapper.toResponse(authUser));
    }

    private Set<Role> createRoles(String... roles) {
        return Arrays.stream(roles)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
