package com.example.messanger.payload.request;

import com.example.messanger.payload.enums.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    private Set<ERole> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}
