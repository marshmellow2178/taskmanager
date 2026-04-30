package com.init330.taskmanager.todo;

import com.init330.taskmanager.user.User;
import com.init330.taskmanager.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public TodoResponseDTO create(
            String title,
            LocalDateTime dueDate
            //User user
    ) throws AccessDeniedException {
        User loginUser = userRepository.findById(1L)
                .orElseThrow(()-> new AccessDeniedException("User not Found"));
        Todo newTodo = Todo.of(title, dueDate, loginUser);
        todoRepository.save(newTodo);
        return TodoResponseDTO.from(newTodo);
    }

    private Todo findById(Long id){
        return todoRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(("Todo Not Found")));
    }

    private void userCheck(Todo todo) throws AccessDeniedException {
        User loginUser = userRepository.findById(1L)
                .orElseThrow(()-> new AccessDeniedException("User not Found"));
        if(!loginUser.getId().equals(todo.getUser().getId())){
            throw new AccessDeniedException(("Access Denied"));
        }
    }

    public TodoResponseDTO searchById(Long id){
        return TodoResponseDTO.from(findById(id));
    }

    public Page<TodoResponseDTO> findByUser(Pageable pageable){
        User loginUser = userRepository.findById(1L).get();
        Page<Todo> page = todoRepository.findByUser(loginUser, pageable);
        //타입변환 람다식은 맨날 까먹지;;
        //람다 -> 메서드 표현식(이건또뭐여)
        return page.map(TodoResponseDTO::from);
    }

    @Transactional
    public TodoResponseDTO complete(Long id) throws AccessDeniedException {
        Todo todo =  findById(id);
        userCheck(todo);
        todo.setCompletedTrue();
        return TodoResponseDTO.from(todo);
    }

    @Transactional
    public TodoResponseDTO unComplete(Long id) throws AccessDeniedException {
        Todo todo =  findById(id);
        userCheck(todo);
        todo.setCompletedFalse();
        return TodoResponseDTO.from(todo);
    }

    @Transactional
    public TodoResponseDTO update(Long id, String title, LocalDateTime dueDate)
            throws AccessDeniedException {
        Todo todo = findById(id);
        userCheck(todo);
        todo.updateTodo(title, dueDate);
        return TodoResponseDTO.from(todo);
    }

    public void delete(Long id) throws AccessDeniedException {
        Todo todo = findById(id);
        userCheck(todo);
        todoRepository.delete(todo);
    }
}
