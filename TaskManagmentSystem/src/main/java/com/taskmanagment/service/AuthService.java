package com.taskmanagment.service;

import com.taskmanagment.dto.jwt.JwtRequest;
import com.taskmanagment.dto.jwt.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest jwtRequest);
    JwtResponse getAccessToken(String refreshToken);
    JwtResponse refresh(String refreshToken);
}
