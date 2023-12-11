package com.taskmanagment.dto.request;

import com.taskmanagment.model.enums.ActivityStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {

    private UUID commentId;

    @NotBlank(message = "Body can not be NULL or EMPTY")
    private String body;

    private UUID taskId;
    private ActivityStatus availability;
}
