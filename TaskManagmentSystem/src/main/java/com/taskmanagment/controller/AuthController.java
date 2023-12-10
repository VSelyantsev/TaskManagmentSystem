package com.taskmanagment.controller;

import com.taskmanagment.api.AuthApi;
import com.taskmanagment.dto.jwt.JwtRequest;
import com.taskmanagment.dto.jwt.JwtResponse;
import com.taskmanagment.dto.jwt.RefreshJwtRequest;
import com.taskmanagment.service.AuthService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<JwtResponse> login(@NonNull JwtRequest jwtRequest) {
        final JwtResponse token = authService.login(jwtRequest);
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<JwtResponse> getNewAccessToken(@NonNull RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<JwtResponse> getNewRefreshToken(@NonNull RefreshJwtRequest request) {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
