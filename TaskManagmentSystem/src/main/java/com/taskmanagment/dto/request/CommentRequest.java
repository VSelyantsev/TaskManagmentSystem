package com.taskmanagment.dto.request;

import com.taskmanagment.model.enums.ActivityStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {
    private UUID commentId;
    private String body;
    private UUID taskId;
    private ActivityStatus availability;
}
