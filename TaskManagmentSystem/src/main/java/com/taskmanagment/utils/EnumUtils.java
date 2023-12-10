package com.taskmanagment.utils;

import com.taskmanagment.model.enums.ExecutionStatus;

import java.util.Arrays;

public class EnumUtils {
    public static ExecutionStatus extractStatusFromString(String executionStatus) {
        return Arrays.stream(ExecutionStatus.values())
                .filter(status -> status.getName().equals(executionStatus))
                .findFirst()
                .orElse(ExecutionStatus.COMPLETED);
    }
}
