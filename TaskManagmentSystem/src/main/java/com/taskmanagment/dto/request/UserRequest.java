package com.taskmanagment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @Email
    @NotBlank(message = "Login can not be NULL or EMPTY")
    private String login;

    @Size(min = 6, max = 15)
    @NotBlank(message = "Password can not be NULL or EMPTY")
    private String password;
}
