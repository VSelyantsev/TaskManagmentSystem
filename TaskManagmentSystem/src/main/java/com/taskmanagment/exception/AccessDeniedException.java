package com.taskmanagment.exception;

import com.taskmanagment.model.enums.ActivityStatus;

public class AccessDeniedException extends org.springframework.security.access.AccessDeniedException {
    public AccessDeniedException(ActivityStatus availability) {
        super(String.format("Permission to this resource is DENIED, your availability status is %s", availability));
    }
}
