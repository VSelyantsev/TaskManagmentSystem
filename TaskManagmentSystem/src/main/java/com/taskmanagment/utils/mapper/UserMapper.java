package com.taskmanagment.utils.mapper;

import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring", imports = {TaskMapper.class, CommentMapper.class})
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "availability", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "hashPassword", expression = "java(UserMapper.getHashPassword(userRequest))")
    User toEntity(UserRequest userRequest);


    UserResponse toResponse(User user);

    static String getHashPassword(UserRequest userRequest) {
        return new BCryptPasswordEncoder().encode(userRequest.getPassword());
    }
}
