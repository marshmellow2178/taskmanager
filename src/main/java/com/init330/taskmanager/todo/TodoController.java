package com.init330.taskmanager.todo;

import com.init330.taskmanager.auth.UserPrincipal;
import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;
    private final UserService userService;

    private User loginUser(UserPrincipal principal) {
        return userService.findById(principal.id());
    }

    @GetMapping
    public ResponseEntity<?> getTodos(
            @PageableDefault Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal){
        Page<TodoResponseDTO> todoPage = todoService.findByUser(pageable, loginUser(principal));
        return ResponseEntity.ok(todoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTodo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws AccessDeniedException {
        return ResponseEntity.ok().body(todoService.searchById(id, loginUser(principal)));
    }

    @PostMapping
    public ResponseEntity<?> createTodo(
            @Valid @RequestBody TodoRequestDTO todoRequestDTO,
            @AuthenticationPrincipal UserPrincipal principal)
            throws AccessDeniedException {
        TodoResponseDTO todo = todoService.create(todoRequestDTO.title(), todoRequestDTO.dueDate(), loginUser(principal));
        return ResponseEntity.ok().body(todo);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatusTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoStatusRequestDTO todoStatusRequestDTO,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws AccessDeniedException {
        return ResponseEntity.ok().body(todoService.changeComplete(id, todoStatusRequestDTO.completed(), loginUser(principal)));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<?> updateTodo(
            @Valid @RequestBody TodoRequestDTO todoRequestDTO,
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws AccessDeniedException {
        return ResponseEntity.ok().body(todoService.update(id, todoRequestDTO.title(), todoRequestDTO.dueDate(), loginUser(principal)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws AccessDeniedException {
        todoService.delete(id, loginUser(principal));
        return ResponseEntity.noContent().build();
    }

}
