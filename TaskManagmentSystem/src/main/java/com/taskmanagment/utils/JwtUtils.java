package com.taskmanagment.utils;

import com.taskmanagment.filter.JwtAuthentication;
import com.taskmanagment.model.enums.Role;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtUtils {
    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setLogin(claims.get("login", String.class));
        jwtInfoToken.setUserId(UUID.fromString(claims.get("userId", String.class)));
        return jwtInfoToken;
    }

    private static Set<Role> getRoles(Claims claims) {
        final List<String> roles = claims.get("roles", List.class);

        return roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
