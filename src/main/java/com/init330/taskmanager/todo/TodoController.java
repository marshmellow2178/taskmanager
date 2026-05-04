package com.init330.taskmanager.todo;

import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserRequestDTO;
import com.init330.taskmanager.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getTodos(
            @PageableDefault Pageable pageable){
        Page<TodoResponseDTO> todoPage = todoService.findByUser(pageable, userService.findById(1L));
        return ResponseEntity.ok(todoPage);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(
            @Valid
            @RequestBody TodoRequestDTO todoRequestDTO) throws AccessDeniedException {
        TodoResponseDTO todo = todoService.create(todoRequestDTO.title(), todoRequestDTO.dueDate());
        return ResponseEntity.ok().body(todo);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeTodo(
            @PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok().body(todoService.complete(id));
    }

    @PatchMapping("/{id}/uncompleted")
    public ResponseEntity<?> unCompleteTodo(
            @PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok().body(todoService.unComplete(id));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteTodo(
            @PathVariable Long id
    ) throws AccessDeniedException {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
