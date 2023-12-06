package com.taskmanagment.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTaskResponse {
    private UUID userId;
    private List<TaskResponse> responses;
}
