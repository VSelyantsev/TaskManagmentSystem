package com.taskmanagment.api;

import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// add validation
@RequestMapping(value = "api/users")
@Tag(name = "User", description = "The User Api")
@Api(description = "Api for performing interaction with User entity")
public interface UserApi {

    @Operation(summary = "Create User by UserRequest")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Return UserResponse Performance", response = UserResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
           }
    )
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse create(@RequestBody UserRequest userRequest);

    @Operation(summary = "Find user by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return UserResponse Performance", response = UserResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET,
            value = "{userId}"
    )
    @ResponseStatus(HttpStatus.OK)
    UserResponse findUserById(@PathVariable UUID userId);

    @Operation(summary = "Find all users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return List of UserResponse", response = List.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    List<UserResponse> findAll();

    @Operation(summary = "Update User by Id and Return UserResponse")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return Updated UserResponse", response = UserResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "User Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT,
            value = "{userId}"
    )
    @ResponseStatus(HttpStatus.OK)
    UserResponse update(@PathVariable UUID userId, @RequestBody UserRequest userRequest);

    @Operation(summary = "Delete User by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deleted Successfully", response = Void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "User Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Void.class)
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "{userId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID userId);
}
