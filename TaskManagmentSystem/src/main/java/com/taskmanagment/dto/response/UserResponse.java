package com.taskmanagment.dto.response;

import com.taskmanagment.model.enums.ActivityStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String login;
    private ActivityStatus availability;
}
