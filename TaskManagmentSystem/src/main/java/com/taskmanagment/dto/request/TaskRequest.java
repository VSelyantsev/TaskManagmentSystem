package com.taskmanagment.dto.request;

import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.model.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskRequest {

    private UUID taskId;

    @NotBlank(message = "Title can not be NULL or EMPTY")
    private String title;

    @NotBlank(message = "Description can not be NULL or EMPTY ")
    private String description;
    private UUID authorId;
    private UUID executorId;
    private ExecutionStatus status;

    @NotNull(message = "Task Priority can not be NULL")
    private Priority taskPriority;
    private ActivityStatus availability;
}
