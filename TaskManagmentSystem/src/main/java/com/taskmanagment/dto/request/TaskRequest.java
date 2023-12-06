package com.taskmanagment.dto.request;

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
public class TaskRequest {
    private UUID taskId;
    private String title;
    private String description;
    private UUID authorId;
    private UUID executorId;
    private ExecutionStatus status;
    private Priority taskPriority;
    private ActivityStatus availability;
}
