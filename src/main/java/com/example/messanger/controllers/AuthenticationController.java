package com.example.messanger.controllers;

import com.example.messanger.payload.enums.EStatus;
import com.example.messanger.payload.request.LoginRequest;
import com.example.messanger.payload.request.CreateUserRequest;
import com.example.messanger.payload.response.MessageResponse;
import com.example.messanger.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }

    @PostMapping("/create/user")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        Boolean isUserCreated = authenticationService.createUser(createUserRequest);

        if (isUserCreated) {
            return ResponseEntity.ok(new MessageResponse(EStatus.SUCCESS, ""));
        } else {
            return ResponseEntity.ok(new MessageResponse(EStatus.FAILURE, "User already exists"));
        }
    }
}
