package ru.previak.restaurant.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.previak.restaurant.requests.LoginRequest;
import ru.previak.restaurant.requests.RegisterRequest;
import ru.previak.restaurant.responses.AuthenticationResponse;
import ru.previak.restaurant.services.interfaces.AuthenticationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/auth")
@Api("Authentication controller")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    @ApiOperation("Register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
            ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    @PostMapping("/login")
    @ApiOperation("Login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody LoginRequest request
            ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
