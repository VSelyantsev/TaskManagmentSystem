package com.taskmanagment.api;

import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserTaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "Api for performing User interactions with Task model")
@RequestMapping(value = "api/user/task")
public interface UserInteractionApi {

    @Operation(summary = "Appoint An Executor to Task")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return UserTaskResponse",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserTaskResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    UserTaskResponse appointExecutor(@RequestParam UUID userId, @RequestParam UUID taskId);


    @Operation(summary = "Change Task Status by TaskId and Execution Status")
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
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PATCH,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.OK)
    TaskResponse changeTaskStatus(@PathVariable UUID taskId, @RequestBody String executionStatus);


    @Operation(
            summary = "Return Page of TaskResponses of specific author or executor",
            description = "It's sort by Priority Task"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return Page with responses",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Page.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET,
            value = "{userId}"
    )
    @ResponseStatus(HttpStatus.OK)
    Page<TaskResponse> findAllPage(
            @Parameter(name = "Page offset defaultValue = 0") @RequestParam(defaultValue = "0") int offset,
            @Parameter(name = "Page size defaultValue = 10") @RequestParam(defaultValue = "10") int pageSize,
            @PathVariable UUID userId
    );


}
