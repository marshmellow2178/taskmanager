package com.init330.taskmanager.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TodoRequestDTO(
        @NotBlank @Size(max = 200) String title,
        @NotNull LocalDateTime dueDate
) {
}
