package com.init330.taskmanager.todo;

import com.init330.taskmanager.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {
    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponseDTO create(
            String title,
            LocalDateTime dueDate,
            User loginUser
    ) throws AccessDeniedException {
        log.info("TodoService.create 요청됨");
        Todo newTodo = Todo.of(title, dueDate, loginUser);
        todoRepository.save(newTodo);
        return TodoResponseDTO.from(newTodo);
    }

    private Todo findById(Long id){
        return todoRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(("Todo Not Found")));
    }

    private void userCheck(Todo todo, User loginUser) throws AccessDeniedException {
        if (todo.getUser() == null || !loginUser.getId().equals(todo.getUser().getId())) {
            throw new AccessDeniedException(("Access Denied"));
        }
    }

    public TodoResponseDTO searchById(Long id, User loginUser) throws AccessDeniedException {
        log.info("TodoService.searchById 요청됨");
        Todo todo = findById(id);
        userCheck(todo, loginUser);
        return TodoResponseDTO.from(todo);
    }

    public Page<TodoResponseDTO> findByUser(Pageable pageable, User loginUser){
        log.info("TodoService.findByUser 요청됨");;
        Page<Todo> page = todoRepository.findByUser(loginUser, pageable);
        //타입변환 람다식은 맨날 까먹지;;
        //람다 -> 메서드 표현식(이건또뭐여)
        return page.map(TodoResponseDTO::from);
    }

    @Transactional
    public TodoResponseDTO changeComplete(Long id, boolean complete, User loginUser) throws AccessDeniedException {
        Todo todo = findById(id);
        userCheck(todo, loginUser);
        todo.setCompleted(complete);
        return TodoResponseDTO.from(todo);
    }

    @Transactional
    public TodoResponseDTO update(Long id, String title, LocalDateTime dueDate, User loginUser)
            throws AccessDeniedException {
        log.info("TodoService.update 요청됨");
        Todo todo = findById(id);
        userCheck(todo, loginUser);
        todo.updateTodo(title, dueDate);
        return TodoResponseDTO.from(todo);
    }

    public void delete(Long id, User loginUser) throws AccessDeniedException {
        log.info("TodoService.delete 요청됨");
        Todo todo = findById(id);
        userCheck(todo, loginUser);
        todoRepository.delete(todo);
    }
}
