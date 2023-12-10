package com.taskmanagment.api;

import com.taskmanagment.dto.request.UserRequest;
import com.taskmanagment.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "Api for performing interaction with User entity")
@RequestMapping(value = "api/users")
public interface UserApi {

    @Operation(summary = "Create User by UserRequest")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Return UserResponse Performance",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
           }
    )
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST,
            value = "/registration"
    )
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse create(@RequestBody UserRequest userRequest);

    @Operation(summary = "Find user by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return UserResponse Performance",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Return List of UserResponse",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = List.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    List<UserResponse> findAll();

    @Operation(summary = "Update User by Id and Return UserResponse")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return Updated UserResponse",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "User Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
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
            @ApiResponse(responseCode = "204", description = "Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "User Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "{userId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID userId);
}
