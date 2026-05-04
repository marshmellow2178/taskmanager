package com.init330.taskmanager.user;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id, String username, String email, LocalDateTime createdDate) {
    public static UserResponseDTO from(User user){
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedDate());
    }
}
