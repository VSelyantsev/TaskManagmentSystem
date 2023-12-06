package com.taskmanagment.api;

import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserTaskResponse;
import com.taskmanagment.model.enums.ExecutionStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "The User Api")
@RequestMapping(value = "api/user/task")
@Api(description = "Api for performing User interactions with Task model")
public interface UserInteractionApi {

    @Operation(summary = "Appoint An Executor to Task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return UserTaskResponse", response = UserTaskResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    UserTaskResponse appointExecutor(@RequestParam UUID userId, @RequestParam UUID taskId);


    @Operation(summary = "Change Task Status by TaskId and Execution Status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return Updated TaskResponse", response = TaskResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.OK)
    TaskResponse changeTaskStatus(@PathVariable UUID taskId, @RequestBody ExecutionStatus executionStatus);
}
