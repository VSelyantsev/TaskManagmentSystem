package com.taskmanagment.dto.response;

import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.model.enums.Priority;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {
    private UUID taskId;
    private String title;
    private String description;
    private UUID authorId;
    private UUID executorId;
    private ExecutionStatus status;
    private Priority taskPriority;
    private ActivityStatus availability;
}
