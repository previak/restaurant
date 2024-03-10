package ru.previak.restaurant.services.interfaces;

import ru.previak.restaurant.requests.LoginRequest;
import ru.previak.restaurant.requests.RegisterRequest;
import ru.previak.restaurant.responses.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(LoginRequest request);
}
