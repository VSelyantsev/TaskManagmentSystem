package com.taskmanagment.service;

import com.taskmanagment.dto.request.CommentRequest;
import com.taskmanagment.dto.response.CommentResponse;
import com.taskmanagment.dto.response.TaskResponse;
import com.taskmanagment.dto.response.UserResponse;
import com.taskmanagment.exception.NotFoundException;
import com.taskmanagment.model.Comment;
import com.taskmanagment.model.Task;
import com.taskmanagment.model.User;
import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.repository.CommentRepository;
import com.taskmanagment.service.impl.CommentServiceImpl;
import com.taskmanagment.service.impl.TaskServiceImpl;
import com.taskmanagment.service.impl.UserServiceImpl;
import com.taskmanagment.utils.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TaskServiceImpl taskService;

    private static final UUID VALID_UUID = UUID.fromString("67b24186-cfeb-4d41-99ff-2ce2fe7c975c");
    private static final UUID VALID_TASK_UUID = UUID.fromString("cdb92f9d-911a-4d18-ac6f-56d3fe836aa2");
    private static final UUID VALID_COMMENT_UUID_2 = UUID.fromString("cdb92f9d-911a-4d18-ac6f-56d3fe836aa3");
    private static final UUID VALID_COMMENT_UUID = UUID.fromString("cdb92f9d-911a-4d18-ac6f-56d3fe836aa4");

    private static final UserResponse AUTH_RESPONSE = UserResponse.builder()
            .userId(VALID_UUID)
            .availability(ActivityStatus.AVAILABLE)
            .build();

    private static final CommentRequest VALID_REQUEST = CommentRequest.builder()
            .commentId(VALID_COMMENT_UUID)
            .body("test")
            .build();

    private static final Comment VALID_COMMENT = Comment.builder()
            .commentId(VALID_COMMENT_UUID)
            .user(User.builder().userId(AUTH_RESPONSE.getUserId()).build())
            .body(VALID_REQUEST.getBody())
            .task(Task.builder().taskId(VALID_TASK_UUID).build())
            .availability(VALID_REQUEST.getAvailability())
            .build();

    @Test
    void create_shouldReturnCommentResponse() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(taskService.findTaskById(VALID_TASK_UUID)).thenAnswer(invocation -> {
            UUID taskId = invocation.getArgument(0);
            return TaskResponse.builder()
                    .taskId(taskId)
                    .build();
        });

        when(commentMapper.toEntity(any(CommentRequest.class))).thenReturn(VALID_COMMENT);
        when(commentRepository.save(any(Comment.class))).thenReturn(VALID_COMMENT);
        when(commentMapper.toResponse(VALID_COMMENT)).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            return CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .taskId(comment.getTask().getTaskId())
                    .availability(comment.getAvailability())
                    .build();
        });

        CommentResponse actualResponse = commentService.create(VALID_TASK_UUID, VALID_REQUEST);
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getTaskId(), VALID_TASK_UUID);

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(taskService, times(1)).findTaskById(VALID_TASK_UUID);
        verify(commentMapper, times(1)).toEntity(any(CommentRequest.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).toResponse(VALID_COMMENT);
    }

    @Test
    void findById_shouldReturnCommentResponse() throws Exception {
        CommentResponse expectedResponse = CommentResponse.builder()
                .commentId(VALID_COMMENT.getCommentId())
                .body(VALID_COMMENT.getBody())
                .taskId(VALID_COMMENT.getTask().getTaskId())
                .availability(VALID_COMMENT.getAvailability())
                .build();


        when(commentRepository.findById(VALID_COMMENT_UUID)).thenReturn(Optional.of(VALID_COMMENT));
        when(commentMapper.toResponse(VALID_COMMENT)).thenReturn(expectedResponse);

        CommentResponse actualResponse = commentService.findCommentById(VALID_COMMENT_UUID);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getCommentId(), actualResponse.getCommentId());
        assertEquals(expectedResponse.getTaskId(), actualResponse.getTaskId());
        assertEquals(expectedResponse.getBody(), actualResponse.getBody());
        assertEquals(expectedResponse.getAvailability(), actualResponse.getAvailability());

        verify(commentRepository, times(1)).findById(VALID_COMMENT_UUID);
        verify(commentMapper, times(1)).toResponse(VALID_COMMENT);
    }

    @Test
    void findById_withInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(commentRepository.findById(VALID_COMMENT_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.findCommentById(VALID_COMMENT_UUID));

        verify(commentRepository, times(1)).findById(VALID_COMMENT_UUID);
    }

    @Test
    void findAll_shouldReturnListOfCommentResponses() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));

        Comment comment1 = Comment.builder()
                .commentId(VALID_COMMENT_UUID)
                .user(User.builder().userId(AUTH_RESPONSE.getUserId()).build())
                .availability(ActivityStatus.AVAILABLE)
                .build();

        Comment comment2 = Comment.builder()
                .commentId(VALID_COMMENT_UUID_2)
                .user(User.builder().userId(AUTH_RESPONSE.getUserId()).build())
                .availability(ActivityStatus.AVAILABLE)
                .build();

        List<Comment> commentList = List.of(comment1, comment2);

        CommentResponse commentResponse1 = CommentResponse.builder()
                .commentId(comment1.getCommentId())
                .authorId(comment1.getUser().getUserId())
                .availability(comment1.getAvailability())
                .build();

        CommentResponse commentResponse2 = CommentResponse.builder()
                .commentId(comment2.getCommentId())
                .authorId(comment2.getUser().getUserId())
                .availability(comment2.getAvailability())
                .build();

        List<CommentResponse> expectedResponses = List.of(commentResponse1, commentResponse2);

        when(commentRepository.findAll()).thenReturn(commentList);

        when(commentMapper.toResponse(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            return CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .availability(comment.getAvailability())
                    .authorId(comment.getUser().getUserId())
                    .build();
        });

        List<CommentResponse> actualResponse = commentService.findAll();

        assertNotNull(actualResponse);
        assertEquals(expectedResponses.size(), actualResponse.size());

        verify(commentRepository, times(1)).findAll();
        verify(commentMapper, times(2)).toResponse(any(Comment.class));
    }

    @Test
    void update_shouldReturnUpdatedCommentResponse() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(commentRepository.findById(VALID_COMMENT_UUID)).thenReturn(Optional.of(VALID_COMMENT));

        CommentResponse expectedResponse = CommentResponse.builder()
                .body("updatedBody")
                .build();

        when(commentRepository.save(VALID_COMMENT)).thenReturn(any(Comment.class));
        when(commentMapper.toResponse(VALID_COMMENT)).thenReturn(expectedResponse);

        CommentResponse actualResponse = commentService.update(VALID_COMMENT_UUID, VALID_REQUEST);
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getBody(), expectedResponse.getBody());

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(commentRepository, times(1)).findById(VALID_COMMENT_UUID);
        verify(commentRepository, times(1)).save(VALID_COMMENT);
        verify(commentMapper, times(1)).toResponse(VALID_COMMENT);
    }

    @Test
    void update_withInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(commentRepository.findById(VALID_COMMENT_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.update(VALID_COMMENT_UUID, VALID_REQUEST));

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(commentRepository, times(1)).findById(VALID_COMMENT_UUID);
    }

    @Test
    void delete_void_checkIfAvailabilityChanged() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(commentRepository.findById(VALID_COMMENT_UUID)).thenReturn(Optional.of(VALID_COMMENT));

        Comment unvailableComment = Comment.builder()
                .commentId(VALID_COMMENT_UUID)
                .availability(ActivityStatus.NOT_AVAILABLE)
                .user(User.builder().userId(AUTH_RESPONSE.getUserId()).build())
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(unvailableComment);
        commentService.delete(VALID_COMMENT_UUID);

        assertEquals(unvailableComment.getAvailability(), ActivityStatus.NOT_AVAILABLE);

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(commentRepository, times(1)).findById(VALID_COMMENT_UUID);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void delete_withInvalidId_shouldThrowNotFoundKeyException() throws Exception {
        when(userService.getCurrentAuthenticationUser()).thenReturn(Optional.of(AUTH_RESPONSE));
        when(commentRepository.findById(VALID_COMMENT_UUID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.delete(VALID_COMMENT_UUID));

        verify(userService, times(1)).getCurrentAuthenticationUser();
        verify(commentRepository, times(1)).findById(VALID_COMMENT_UUID);
    }

}
