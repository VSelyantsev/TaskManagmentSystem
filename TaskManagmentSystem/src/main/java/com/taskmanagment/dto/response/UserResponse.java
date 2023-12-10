package com.taskmanagment.dto.response;

import com.taskmanagment.model.enums.ActivityStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID userId;
    private String login;
    private ActivityStatus availability;
}
