package com.taskmanagment.api;

import com.taskmanagment.dto.request.CommentRequest;
import com.taskmanagment.dto.response.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Comment", description = "Api for performing interaction with Comment model")
@RequestMapping(value = "api/comments")
public interface CommentApi {

    @Operation(summary = "Create Comment by CommentRequest and appoint it to an Task")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return CommentResponse and appoint it to an Task",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(ref = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST,
            value = "{taskId}"
    )
    @ResponseStatus(HttpStatus.CREATED)
    CommentResponse create(@PathVariable UUID taskId, @Valid @RequestBody CommentRequest commentRequest);

    @Operation(summary = "Find Comment by Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return CommentResponse",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Return List of Comments",
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
    List<CommentResponse> findAll();

    @Operation(summary = "Update Comment by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return updated CommentResponse",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.PUT,
            value = "{commentId}"
    )
    @ResponseStatus(HttpStatus.OK)
    CommentResponse update(@PathVariable UUID commentId, @Valid @RequestBody CommentRequest commentRequest);

    @Operation(summary = "Delete Comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "{commentId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID commentId);
}
