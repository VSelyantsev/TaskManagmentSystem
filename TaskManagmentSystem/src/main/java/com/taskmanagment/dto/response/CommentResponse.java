package com.taskmanagment.dto.response;

import com.taskmanagment.model.enums.ActivityStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private UUID commentId;
    private String body;
    private UUID taskId;
    private UUID authorId;
    private ActivityStatus availability;
}
