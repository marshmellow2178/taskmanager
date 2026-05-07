package com.init330.taskmanager.auth;

public record TokenResponseDTO(String tokenType, String accessToken) {
    public static TokenResponseDTO bearer(String accessToken) {
        return new TokenResponseDTO("Bearer", accessToken);
    }
}

