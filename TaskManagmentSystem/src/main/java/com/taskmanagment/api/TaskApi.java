package com.taskmanagment.api;

import com.taskmanagment.dto.request.TaskRequest;
import com.taskmanagment.dto.response.TaskResponse;
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

@Tag(name = "Task", description = "Api for performing interaction with Task model")
@RequestMapping(value = "api/tasks")
public interface TaskApi {

    @Operation(summary = "Create Task by TaskRequest")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return TaskResponse performance",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Return TaskResponse performance",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.OK)
    TaskResponse finTaskById(@PathVariable UUID taskId);

    @Operation(summary = "Searches for tasks in which the authorized user is their EXECUTOR")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return List of Tasks",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = List.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    List<TaskResponse> findAll();

    @Operation(summary = "Update Task by taskId and TaskRequest")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return List of Tasks",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Return Updated TaskResponse",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PATCH,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.OK)
    TaskResponse updateTaskStatus(@PathVariable UUID taskId, @RequestBody String executionStatus);

    @Operation(summary = "Delete Task by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted Successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Task Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID taskId);
}
