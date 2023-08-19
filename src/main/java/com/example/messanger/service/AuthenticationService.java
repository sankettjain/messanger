package com.example.messanger.service;

import com.example.messanger.payload.request.CreateUserRequest;
import com.example.messanger.payload.request.LoginRequest;
import com.example.messanger.payload.response.JwtResponse;
import com.example.messanger.payload.response.MessageResponse;

public interface AuthenticationService {

    JwtResponse login(LoginRequest loginRequest);

    Boolean createUser(CreateUserRequest createUserRequest);


}
