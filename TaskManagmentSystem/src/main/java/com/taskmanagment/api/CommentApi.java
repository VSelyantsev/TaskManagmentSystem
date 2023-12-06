package com.taskmanagment.api;

import com.taskmanagment.dto.request.CommentRequest;
import com.taskmanagment.dto.response.CommentResponse;
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

@Tag(name = "Comment", description = "The Comment Api")
@RequestMapping(value = "api/comments")
@Api(description = "Api for performing interaction with Comment model")
public interface CommentApi {

    @Operation(summary = "Create Comment by CommentRequest and appoint it to an Task")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 201,
                    message = "Return CommentResponse and appoint it to an Task",
                    response = CommentResponse.class
            ),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.CREATED)
    CommentResponse create(@PathVariable UUID taskId, @RequestBody CommentRequest commentRequest);

    @Operation(summary = "Find Comment by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return CommentResponse", response = CommentResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET,
            value = "{commentId}"
    )
    @ResponseStatus(HttpStatus.OK)
    CommentResponse findCommentById(@PathVariable UUID commentId);

    @Operation(summary = "Find all Comments")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return List of Comments", response = List.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    List<CommentResponse> findAll();

    @Operation(summary = "Update Comment by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return updated CommentResponse", response = CommentResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT,
            value = "{commentId}"
    )
    @ResponseStatus(HttpStatus.OK)
    CommentResponse update(@PathVariable UUID commentId, @RequestBody CommentRequest commentRequest);

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "{commentId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID commentId);
}
