package com.init330.taskmanager.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank String usernameOrEmail,
        @NotBlank String password
) {
}

