package com.init330.taskmanager.todo;

import java.time.LocalDateTime;

public record TodoRequestDTO(String title,
                             LocalDateTime dueDate) {
}
