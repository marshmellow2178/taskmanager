package com.init330.taskmanager.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank @Size(max = 100) String usernameOrEmail,
        @NotBlank @Size(max = 100) String password
) {
}

