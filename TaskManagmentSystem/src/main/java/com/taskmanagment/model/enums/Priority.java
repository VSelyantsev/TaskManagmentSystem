package com.taskmanagment.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {
    HIGH("HIGH"),
    MIDDLE("MIDDLE"),
    LOW("LOW");

    private final String name;
}
