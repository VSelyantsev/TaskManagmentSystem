package com.taskmanagment.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExecutionStatus {
    WAITING("WAITING"),
    IN_PROCESS("IN_PROGRESS"),
    COMPLETED("COMPLETED");

    private final String name;
}
