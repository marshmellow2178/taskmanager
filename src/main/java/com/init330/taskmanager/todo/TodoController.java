package com.init330.taskmanager.todo;

import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserRequestDTO;
import com.init330.taskmanager.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> index(){
        return ResponseEntity.status(200).body("Hello world!");
    }

    @GetMapping("/init")
    public ResponseEntity<?> setInit(
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.DESC) Pageable pageable) throws AccessDeniedException {
        //유저등록(임시코드)
        UserRequestDTO userDTO = new UserRequestDTO("username", "email", "password");
        userService.create(userDTO);
        TodoRequestDTO todoDTO = new TodoRequestDTO("제목", LocalDateTime.now());
        todoService.create(todoDTO.title(), todoDTO.dueDate() );
        return ResponseEntity.ok().body(todoService.findByUser(pageable));
    }

    @PatchMapping("/complete/{id}")
    public ResponseEntity<?> completeTodo(
            @PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok().body(todoService.complete(id));
    }

    @PatchMapping("/uncompleted/{id}")
    public ResponseEntity<?> unCompleteTodo(
            @PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok().body(todoService.unComplete(id));
    }
    //RestControllerAdvice: 500 -> 404


}
