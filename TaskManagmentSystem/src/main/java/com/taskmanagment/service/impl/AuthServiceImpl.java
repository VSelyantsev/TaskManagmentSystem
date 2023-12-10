package com.taskmanagment.service.impl;

import com.taskmanagment.dto.jwt.JwtRequest;
import com.taskmanagment.dto.jwt.JwtResponse;
import com.taskmanagment.exception.AuthenticationException;
import com.taskmanagment.exception.NotFoundLoginNameException;
import com.taskmanagment.filter.JwtProvider;
import com.taskmanagment.model.User;
import com.taskmanagment.repository.UserRepository;
import com.taskmanagment.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder encoder;


    @Override
    public JwtResponse login(JwtRequest jwtRequest) {
        final User existingUser = userRepository.findByLogin(jwtRequest.getLogin())
                .orElseThrow(() -> new NotFoundLoginNameException(jwtRequest.getLogin()));
        if (encoder.matches(jwtRequest.getPassword(), existingUser.getHashPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(existingUser);
            final String refreshToken = jwtProvider.generateAccessToken(existingUser);
            refreshStorage.put(existingUser.getLogin(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthenticationException("Incorrect Password");
        }
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims  = jwtProvider.getAccessClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken  = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User existingUser = userRepository.findByLogin(login)
                        .orElseThrow(() -> new NotFoundLoginNameException(login));
                final String accessToken = jwtProvider.generateAccessToken(existingUser);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User existingUser = userRepository.findByLogin(login)
                        .orElseThrow(() -> new NotFoundLoginNameException(login));
                final String accessToken = jwtProvider.generateAccessToken(existingUser);
                final String newRefreshToken = jwtProvider.generateRefreshToken(existingUser);
                refreshStorage.put(existingUser.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, refreshToken);
            }
        }
        throw new AuthenticationException("Invalid JWT token");
    }
}
