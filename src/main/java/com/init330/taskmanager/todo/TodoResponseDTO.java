package com.init330.taskmanager.todo;

import java.time.LocalDateTime;

public record TodoResponseDTO(String title,
                              LocalDateTime dueDate,
                              boolean completed,
                              LocalDateTime createdDate) {

    public static TodoResponseDTO from(Todo todo){ //레코드 팩토리 메서드
        return new TodoResponseDTO(todo.getTitle(), todo.getDueDate(), todo.isCompleted(), todo.getCreatedDate());
    }
}
