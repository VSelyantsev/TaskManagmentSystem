package com.taskmanagment.model.enums;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }
}
