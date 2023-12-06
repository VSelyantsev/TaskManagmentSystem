package com.taskmanagment.api;

import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.model.enums.ExecutionStatus;
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

@Tag(name = "Task", description = "The Task Api")
@RequestMapping(value = "api/tasks")
@Api(description = "Api for performing interaction with Task model")
public interface TaskApi {

    @Operation(summary = "Create Task by TaskRequest")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Return TaskResponse performance", response = TaskResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST
    )
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse create(@RequestBody TaskRequest taskRequest);

    @Operation(summary = "Find Task by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return TaskResponse performance", response = TaskResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.OK)
    TaskResponse finTaskById(@PathVariable UUID taskId);

    @Operation(summary = "Find all Tasks")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return List of Tasks", response = List.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    List<TaskResponse> findAll();

    @Operation(summary = "Update Task by taskId and TaskRequest")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return List of Tasks", response = TaskResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.OK)
    TaskResponse update(@PathVariable UUID taskId, @RequestBody TaskRequest taskRequest);

    @Operation(summary = "Update Task Status by Task Id and ExecutionStatus")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return Updated TaskResponse", response = TaskResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PATCH,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.OK)
    TaskResponse updateTaskStatus(@PathVariable UUID taskId, @RequestBody ExecutionStatus executionStatus);

    @Operation(summary = "Delete Task by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deleted Successfully", response = Void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Task Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Void.class)
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID taskId);
}
