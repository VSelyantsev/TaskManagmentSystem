package com.taskmanagment.api;

import com.taskmanagment.dto.jwt.JwtRequest;
import com.taskmanagment.dto.jwt.JwtResponse;
import com.taskmanagment.dto.jwt.RefreshJwtRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value = "api/auth")
@Tag(name =   "Authentication", description = "Authentication operations" )
public interface AuthApi {

    @Operation(summary = "Login with Jwt")
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/login"
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<JwtResponse> login(
            @Parameter(name = "Jwt Request object", required = true)
            @NonNull @RequestBody JwtRequest jwtRequest
    );

    @Operation(summary = "Get new Access Token")
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/token"
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<JwtResponse> getNewAccessToken(
            @Parameter(name = "Refresh Token Request object", required = true)
            @NonNull @RequestBody RefreshJwtRequest request
    );

    @Operation(summary = "Get new Refresh Token")
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/refresh"
    )
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<JwtResponse> getNewRefreshToken(
            @Parameter(name = "Refresh Token Request object", required = true)
            @NonNull @RequestBody RefreshJwtRequest request
    );


}
